//
// Created by gaoqingguang on 2017/1/12.
//

#include "SkiaApi16.h"

namespace skia_api16 {

////////////////////////////////////////////////////////
// class SkRegion
////////////////////////////////////////////////////////
#define assert_sentinel(value, isSentinel) \
    SkASSERT(((value) == SkRegion::kRunTypeSentinel) == isSentinel)

    static int count_to_intervals(int count) {
        SkASSERT(count >= 6);   // a single rect is 6 values
        return (count - 4) >> 1;
    }

    static int intervals_to_count(int intervals) {
        return 1 + intervals * 4 + 1;
    }
    static int compute_worst_case_count(int a_count, int b_count) {
        int a_intervals = count_to_intervals(a_count);
        int b_intervals = count_to_intervals(b_count);
        // Our heuristic worst case is ai * (bi + 1) + bi * (ai + 1)
        int intervals = 2 * a_intervals * b_intervals + a_intervals + b_intervals;
        // convert back to number of RunType values
        return intervals_to_count(intervals);
    }
    struct spanRec {
        const SkRegion::RunType*    fA_runs;
        const SkRegion::RunType*    fB_runs;
        int                         fA_left, fA_rite, fB_left, fB_rite;
        int                         fLeft, fRite, fInside;

        void init(const SkRegion::RunType a_runs[], const SkRegion::RunType b_runs[])
        {
            fA_left = *a_runs++;
            fA_rite = *a_runs++;
            fB_left = *b_runs++;
            fB_rite = *b_runs++;

            fA_runs = a_runs;
            fB_runs = b_runs;
        }

        bool done() const
        {
            SkASSERT(fA_left <= SkRegion::kRunTypeSentinel);
            SkASSERT(fB_left <= SkRegion::kRunTypeSentinel);
            return fA_left == SkRegion::kRunTypeSentinel && fB_left == SkRegion::kRunTypeSentinel;
        }

        void next()
        {
            int     inside, left, rite SK_INIT_TO_AVOID_WARNING;
            bool    a_flush = false;
            bool    b_flush = false;

            int a_left = fA_left;
            int a_rite = fA_rite;
            int b_left = fB_left;
            int b_rite = fB_rite;

            if (a_left < b_left)
            {
                inside = 1;
                left = a_left;
                if (a_rite <= b_left)   // [...] <...>
                {
                    rite = a_rite;
                    a_flush = true;
                }
                else // [...<..]...> or [...<...>...]
                    rite = a_left = b_left;
            }
            else if (b_left < a_left)
            {
                inside = 2;
                left = b_left;
                if (b_rite <= a_left)   // [...] <...>
                {
                    rite = b_rite;
                    b_flush = true;
                }
                else // [...<..]...> or [...<...>...]
                    rite = b_left = a_left;
            }
            else    // a_left == b_left
            {
                inside = 3;
                left = a_left;  // or b_left
                if (a_rite <= b_rite)
                {
                    rite = b_left = a_rite;
                    a_flush = true;
                }
                if (b_rite <= a_rite)
                {
                    rite = a_left = b_rite;
                    b_flush = true;
                }
            }

            if (a_flush)
            {
                a_left = *fA_runs++;
                a_rite = *fA_runs++;
            }
            if (b_flush)
            {
                b_left = *fB_runs++;
                b_rite = *fB_runs++;
            }

            SkASSERT(left <= rite);

            // now update our state
            fA_left = a_left;
            fA_rite = a_rite;
            fB_left = b_left;
            fB_rite = b_rite;

            fLeft = left;
            fRite = rite;
            fInside = inside;
        }
    };

    static SkRegion::RunType* operate_on_span(const SkRegion::RunType a_runs[],
                                              const SkRegion::RunType b_runs[],
                                              SkRegion::RunType dst[],
                                              int min, int max)
    {
        spanRec rec;
        bool    firstInterval = true;

        rec.init(a_runs, b_runs);

        while (!rec.done())
        {
            rec.next();

            int left = rec.fLeft;
            int rite = rec.fRite;

            // add left,rite to our dst buffer (checking for coincidence
            if ((unsigned)(rec.fInside - min) <= (unsigned)(max - min) &&
                left < rite)    // skip if equal
            {
                if (firstInterval || dst[-1] < left)
                {
                    *dst++ = (SkRegion::RunType)(left);
                    *dst++ = (SkRegion::RunType)(rite);
                    firstInterval = false;
                }
                else    // update the right edge
                    dst[-1] = (SkRegion::RunType)(rite);
            }
        }

        *dst++ = SkRegion::kRunTypeSentinel;
        return dst;
    }
    static const struct {
        uint8_t fMin;
        uint8_t fMax;
    } gOpMinMax[] = {
            { 1, 1 },   // Difference
            { 3, 3 },   // Intersection
            { 1, 3 },   // Union
            { 1, 2 }    // XOR
    };

    static SkRegion::RunType* skip_scanline(const SkRegion::RunType runs[])
    {
        while (runs[0] != SkRegion::kRunTypeSentinel)
        {
            SkASSERT(runs[0] < runs[1]);    // valid span
            runs += 2;
        }
        return (SkRegion::RunType*)(runs + 1);  // return past the X-sentinel
    }

    class RgnOper {
    public:
        RgnOper(int top, SkRegion::RunType dst[], SkRegion::Op op)
        {
            // need to ensure that the op enum lines up with our minmax array
            SkASSERT(SkRegion::kDifference_Op == 0);
            SkASSERT(SkRegion::kIntersect_Op == 1);
            SkASSERT(SkRegion::kUnion_Op == 2);
            SkASSERT(SkRegion::kXOR_Op == 3);
            SkASSERT((unsigned)op <= 3);

            fStartDst = dst;
            fPrevDst = dst + 1;
            fPrevLen = 0;       // will never match a length from operate_on_span
            fTop = (SkRegion::RunType)(top);    // just a first guess, we might update this

            fMin = gOpMinMax[op].fMin;
            fMax = gOpMinMax[op].fMax;
        }

        void addSpan(int bottom, const SkRegion::RunType a_runs[], const SkRegion::RunType b_runs[])
        {
            SkRegion::RunType*  start = fPrevDst + fPrevLen + 1;    // skip X values and slot for the next Y
            SkRegion::RunType*  stop = operate_on_span(a_runs, b_runs, start, fMin, fMax);
            size_t              len = stop - start;

            if (fPrevLen == len && !memcmp(fPrevDst, start, len * sizeof(SkRegion::RunType)))   // update Y value
                fPrevDst[-1] = (SkRegion::RunType)(bottom);
            else    // accept the new span
            {
                if (len == 1 && fPrevLen == 0) {
                    fTop = (SkRegion::RunType)(bottom); // just update our bottom
                } else {
                    start[-1] = (SkRegion::RunType)(bottom);
                    fPrevDst = start;
                    fPrevLen = len;
                }
            }
        }

        int flush()
        {
            fStartDst[0] = fTop;
            fPrevDst[fPrevLen] = SkRegion::kRunTypeSentinel;
            return (int)(fPrevDst - fStartDst + fPrevLen + 1);
        }

        uint8_t fMin, fMax;

    private:
        SkRegion::RunType*  fStartDst;
        SkRegion::RunType*  fPrevDst;
        size_t              fPrevLen;
        SkRegion::RunType   fTop;
    };

    static int SkRegion_operate(const SkRegion::RunType a_runs[],
                       const SkRegion::RunType b_runs[],
                       SkRegion::RunType dst[],
                       SkRegion::Op op) {
        const SkRegion::RunType gSentinel[] = {
                SkRegion::kRunTypeSentinel,
                // just need a 2nd value, since spanRec.init() reads 2 values, even
                // though if the first value is the sentinel, it ignores the 2nd value.
                // w/o the 2nd value here, we might read uninitialized memory.
                0,
        };

        int a_top = *a_runs++;
        int a_bot = *a_runs++;
        int b_top = *b_runs++;
        int b_bot = *b_runs++;

        assert_sentinel(a_top, false);
        assert_sentinel(a_bot, false);
        assert_sentinel(b_top, false);
        assert_sentinel(b_bot, false);

        RgnOper oper(SkMin32(a_top, b_top), dst, op);

        bool firstInterval = true;
        int prevBot = SkRegion::kRunTypeSentinel; // so we fail the first test

        while (a_bot < SkRegion::kRunTypeSentinel ||
               b_bot < SkRegion::kRunTypeSentinel) {
            int                         top, bot SK_INIT_TO_AVOID_WARNING;
            const SkRegion::RunType*    run0 = gSentinel;
            const SkRegion::RunType*    run1 = gSentinel;
            bool                        a_flush = false;
            bool                        b_flush = false;

            if (a_top < b_top) {
                top = a_top;
                run0 = a_runs;
                if (a_bot <= b_top) {   // [...] <...>
                    bot = a_bot;
                    a_flush = true;
                } else {  // [...<..]...> or [...<...>...]
                    bot = a_top = b_top;
                }
            } else if (b_top < a_top) {
                top = b_top;
                run1 = b_runs;
                if (b_bot <= a_top) {   // [...] <...>
                    bot = b_bot;
                    b_flush = true;
                } else {    // [...<..]...> or [...<...>...]
                    bot = b_top = a_top;
                }
            } else {    // a_top == b_top
                top = a_top;    // or b_top
                run0 = a_runs;
                run1 = b_runs;
                if (a_bot <= b_bot) {
                    bot = b_top = a_bot;
                    a_flush = true;
                }
                if (b_bot <= a_bot) {
                    bot = a_top = b_bot;
                    b_flush = true;
                }
            }

            if (top > prevBot) {
                oper.addSpan(top, gSentinel, gSentinel);
            }
            oper.addSpan(bot, run0, run1);
            if (firstInterval)
                firstInterval = false;

            if (a_flush) {
                a_runs = skip_scanline(a_runs);
                a_top = a_bot;
                a_bot = *a_runs++;
                if (a_bot == SkRegion::kRunTypeSentinel) {
                    a_top = a_bot;
                }
            }
            if (b_flush) {
                b_runs = skip_scanline(b_runs);
                b_top = b_bot;
                b_bot = *b_runs++;
                if (b_bot == SkRegion::kRunTypeSentinel) {
                    b_top = b_bot;
                }
            }

            prevBot = bot;
        }
        return oper.flush();
    }

    struct SkRegion::RunHead {
        int32_t fRefCnt;
        int32_t fRunCount;

        static RunHead *Alloc(int32_t count) {
            //SkDEBUGCODE(sk_atomic_inc(&gRgnAllocCounter);)
            //SkDEBUGF(("************** gRgnAllocCounter::alloc %d\n", gRgnAllocCounter));

            SkASSERT(count >= SkRegion::kRectRegionRuns);

            RunHead *head = (RunHead *) sk_malloc(sizeof(RunHead) + count * sizeof(RunType));
            head->fRefCnt = 1;
            head->fRunCount = count;
            return head;
        }

        bool isComplex() const {
            return this != SkRegion_gEmptyRunHeadPtr && this != SkRegion_gRectRunHeadPtr;
        }

        SkRegion::RunType *writable_runs() {
            SkASSERT(this->isComplex());
            SkASSERT(fRefCnt == 1);
            return (SkRegion::RunType *) (this + 1);
        }

        const SkRegion::RunType *readonly_runs() const {
            SkASSERT(this->isComplex());
            return (const SkRegion::RunType *) (this + 1);
        }

        RunHead *ensureWritable() {
            SkASSERT(this->isComplex());

            RunHead *writable = this;
            if (fRefCnt > 1) {
                // We need to alloc & copy the current region before we call
                // sk_atomic_dec because it could be freed in the meantime,
                // otherwise.
                writable = Alloc(fRunCount);
                memcpy(writable->writable_runs(), this->readonly_runs(),
                       fRunCount * sizeof(RunType));

                // fRefCount might have changed since we last checked.
                // If we own the last reference at this point, we need to
                // free the memory.
                if (sk_atomic_dec(&fRefCnt) == 1) {
                    sk_free(this);
                }
            }
            return writable;
        }
    };

    SkRegion::Iterator::Iterator(const SkRegion &rgn) {
        this->reset(rgn);
    }

    bool SkRegion::Iterator::rewind() {
        if (fRgn) {
            this->reset(*fRgn);
            return true;
        }
        return false;
    }

    void SkRegion::Iterator::reset(const SkRegion &rgn) {
        fRgn = &rgn;
        if (rgn.isEmpty()) {
            fDone = true;
        } else {
            fDone = false;
            if (rgn.isRect()) {
                fRect = rgn.fBounds;
                fRuns = NULL;
            } else {
                fRuns = rgn.fRunHead->readonly_runs();
                fRect.set(fRuns[2], fRuns[0], fRuns[3], fRuns[1]);
                fRuns += 4;
            }
        }
    }

    void SkRegion::Iterator::next() {
        if (fDone) {
            return;
        }

        if (fRuns == NULL) {   // rect case
            fDone = true;
            return;
        }

        const RunType *runs = fRuns;

        if (runs[0] < kRunTypeSentinel) { // valid X value
            fRect.fLeft = runs[0];
            fRect.fRight = runs[1];
            runs += 2;
        } else {    // we're at the end of a line
            runs += 1;
            if (runs[0] < kRunTypeSentinel) { // valid Y value
                if (runs[1] == kRunTypeSentinel) {    // empty line
                    fRect.fTop = runs[0];
                    runs += 2;
                } else {
                    fRect.fTop = fRect.fBottom;
                }

                fRect.fBottom = runs[0];
                //assert_sentinel(runs[1], false);
                fRect.fLeft = runs[1];
                fRect.fRight = runs[2];
                runs += 3;
            } else {    // end of rgn
                fDone = true;
            }
        }
        fRuns = runs;
    }

    SkRegion::SkRegion(const SkIRect& rect) {
        fRunHead = SkRegion_gEmptyRunHeadPtr;   // just need a value that won't trigger sk_free(fRunHead)
        this->setRect(rect);
    }

    bool SkRegion::setRect(const SkIRect &r) {
        return this->setRect(r.fLeft, r.fTop, r.fRight, r.fBottom);
    }

    bool SkRegion::setRect(int32_t left, int32_t top,
                           int32_t right, int32_t bottom) {
        if (left >= right || top >= bottom) {
            return this->setEmpty();
        }
        this->freeRuns();
        fBounds.set(left, top, right, bottom);
        fRunHead = SkRegion_gRectRunHeadPtr;
        return true;
    }

    bool SkRegion::setEmpty() {
        this->freeRuns();
        fBounds.set(0, 0, 0, 0);
        fRunHead = SkRegion_gEmptyRunHeadPtr;
        return false;
    }

    void SkRegion::freeRuns() {
        if (fRunHead->isComplex()) {
            SkASSERT(fRunHead->fRefCnt >= 1);
            if (sk_atomic_dec(&fRunHead->fRefCnt) == 1) {
                //SkASSERT(gRgnAllocCounter > 0);
                //SkDEBUGCODE(sk_atomic_dec(&gRgnAllocCounter));
                //SkDEBUGF(("************** gRgnAllocCounter::free %d\n", gRgnAllocCounter));
                sk_free(fRunHead);
            }
        }
    }
    void SkRegion::translate(int dx, int dy, SkRegion* dst) const {
        SkDEBUGCODE(this->validate();)

        if (NULL == dst) {
            return;
        }
        if (this->isEmpty()) {
            dst->setEmpty();
        } else if (this->isRect()) {
            dst->setRect(fBounds.fLeft + dx, fBounds.fTop + dy,
                         fBounds.fRight + dx, fBounds.fBottom + dy);
        } else {
            if (this == dst) {
                dst->fRunHead = dst->fRunHead->ensureWritable();
            } else {
                SkRegion    tmp;
                tmp.allocateRuns(fRunHead->fRunCount);
                tmp.fBounds = fBounds;
                dst->swap(tmp);
            }

            dst->fBounds.offset(dx, dy);

            const RunType*  sruns = fRunHead->readonly_runs();
            RunType*        druns = dst->fRunHead->writable_runs();

            *druns++ = (SkRegion::RunType)(*sruns++ + dy);    // top
            for (;;) {
                int bottom = *sruns++;
                if (bottom == kRunTypeSentinel) {
                    break;
                }
                *druns++ = (SkRegion::RunType)(bottom + dy);  // bottom;
                for (;;) {
                    int x = *sruns++;
                    if (x == kRunTypeSentinel) {
                        break;
                    }
                    *druns++ = (SkRegion::RunType)(x + dx);
                    *druns++ = (SkRegion::RunType)(*sruns++ + dx);
                }
                *druns++ = kRunTypeSentinel;    // x sentinel
            }
            *druns++ = kRunTypeSentinel;    // y sentinel

            SkASSERT(sruns - fRunHead->readonly_runs() == fRunHead->fRunCount);
            SkASSERT(druns - dst->fRunHead->readonly_runs() == dst->fRunHead->fRunCount);
        }

        SkDEBUGCODE(this->validate();)
    }
    void SkRegion::swap(SkRegion& other) {
        SkTSwap<SkIRect>(fBounds, other.fBounds);
        SkTSwap<RunHead*>(fRunHead, other.fRunHead);
    }

    void SkRegion::allocateRuns(int count) {
        fRunHead = RunHead::Alloc(count);
    }
    const SkRegion::RunType* SkRegion::getRuns(RunType tmpStorage[], int* count) const
    {
        SkASSERT(tmpStorage && count);
        const RunType* runs = tmpStorage;

        if (this->isEmpty())
        {
            tmpStorage[0] = kRunTypeSentinel;
            *count = 1;
        }
        else if (this->isRect())
        {
            BuildRectRuns(fBounds, tmpStorage);
            *count = kRectRegionRuns;
        }
        else
        {
            *count = fRunHead->fRunCount;
            runs = fRunHead->readonly_runs();
        }
        return runs;
    }

    bool SkRegion::setRuns(RunType runs[], int count)
    {
        SkDEBUGCODE(this->validate();)
        SkASSERT(count > 0);

        if (count <= 2)
        {
            //  SkDEBUGF(("setRuns: empty\n"));
            assert_sentinel(runs[count-1], true);
            return this->setEmpty();
        }

        // trim off any empty spans from the top and bottom
        // weird I should need this, perhaps op() could be smarter...
        if (count > kRectRegionRuns)
        {
            RunType* stop = runs + count;
            assert_sentinel(runs[0], false);    // top
            assert_sentinel(runs[1], false);    // bottom
            if (runs[2] == SkRegion::kRunTypeSentinel)    // should be first left...
            {
                runs += 2;  // skip empty initial span
                runs[0] = runs[-1]; // set new top to prev bottom
                assert_sentinel(runs[1], false);    // bot: a sentinal would mean two in a row
                assert_sentinel(runs[2], false);    // left
                assert_sentinel(runs[3], false);    // right
            }

            // now check for a trailing empty span
            assert_sentinel(stop[-1], true);
            assert_sentinel(stop[-2], true);
            assert_sentinel(stop[-3], false);   // should be last right
            if (stop[-4] == SkRegion::kRunTypeSentinel)   // eek, stop[-3] was a bottom with no x-runs
            {
                stop[-3] = SkRegion::kRunTypeSentinel;    // kill empty last span
                stop -= 2;
                assert_sentinel(stop[-1], true);
                assert_sentinel(stop[-2], true);
                assert_sentinel(stop[-3], false);
                assert_sentinel(stop[-4], false);
                assert_sentinel(stop[-5], false);
            }
            count = (int)(stop - runs);
        }

        SkASSERT(count >= kRectRegionRuns);

        if (ComputeRunBounds(runs, count, &fBounds))
        {
            //  SkDEBUGF(("setRuns: rect[%d %d %d %d]\n", fBounds.fLeft, fBounds.fTop, fBounds.fRight, fBounds.fBottom));
            return this->setRect(fBounds);
        }

        //  if we get here, we need to become a complex region

        if (!fRunHead->isComplex() || fRunHead->fRunCount != count)
        {
            this->freeRuns();
            this->allocateRuns(count);
        }

        // must call this before we can write directly into runs()
        // in case we are sharing the buffer with another region (copy on write)
        fRunHead = fRunHead->ensureWritable();
        memcpy(fRunHead->writable_runs(), runs, count * sizeof(RunType));

        SkDEBUGCODE(this->validate();)

        return true;
    }

    void SkRegion::BuildRectRuns(const SkIRect& bounds,
                                 RunType runs[kRectRegionRuns])
    {
        runs[0] = bounds.fTop;
        runs[1] = bounds.fBottom;
        runs[2] = bounds.fLeft;
        runs[3] = bounds.fRight;
        runs[4] = kRunTypeSentinel;
        runs[5] = kRunTypeSentinel;
    }
    bool SkRegion::ComputeRunBounds(const SkRegion::RunType runs[], int count, SkIRect* bounds)
    {
        assert_sentinel(runs[0], false);    // top

        if (count == kRectRegionRuns)
        {
            assert_sentinel(runs[1], false);    // bottom
            assert_sentinel(runs[2], false);    // left
            assert_sentinel(runs[3], false);    // right
            assert_sentinel(runs[4], true);
            assert_sentinel(runs[5], true);

            SkASSERT(runs[0] < runs[1]);    // valid height
            SkASSERT(runs[2] < runs[3]);    // valid width

            bounds->set(runs[2], runs[0], runs[3], runs[1]);
            return true;
        }

        int left = SK_MaxS32;
        int rite = SK_MinS32;
        int bot;

        bounds->fTop = *runs++;
        do {
            bot = *runs++;
            if (*runs < SkRegion::kRunTypeSentinel)
            {
                if (left > *runs)
                    left = *runs;
                runs = skip_scanline(runs);
                if (rite < runs[-2])
                    rite = runs[-2];
            }
            else
                runs += 1;  // skip X-sentinel
        } while (runs[0] < SkRegion::kRunTypeSentinel);
        bounds->fLeft = left;
        bounds->fRight = rite;
        bounds->fBottom = bot;
        return false;
    }

    bool SkRegion::op(const SkRegion& rgn, const SkIRect& rect, Op op) {
        SkRegion tmp(rect);

        return this->op(rgn, tmp, op);
    }

    bool SkRegion::op(const SkRegion& rgnaOrig, const SkRegion& rgnbOrig, Op op)
    {
        SkDEBUGCODE(this->validate();)

        SkASSERT((unsigned)op < kOpCount);

        if (kReplace_Op == op)
            return this->set(rgnbOrig);

        // swith to using pointers, so we can swap them as needed
        const SkRegion* rgna = &rgnaOrig;
        const SkRegion* rgnb = &rgnbOrig;
        // after this point, do not refer to rgnaOrig or rgnbOrig!!!

        // collaps difference and reverse-difference into just difference
        if (kReverseDifference_Op == op)
        {
            SkTSwap<const SkRegion*>(rgna, rgnb);
            op = kDifference_Op;
        }

        SkIRect bounds;
        bool    a_empty = rgna->isEmpty();
        bool    b_empty = rgnb->isEmpty();
        bool    a_rect = rgna->isRect();
        bool    b_rect = rgnb->isRect();

        switch (op) {
            case kDifference_Op:
                if (a_empty)
                    return this->setEmpty();
                if (b_empty || !SkIRect::Intersects(rgna->fBounds, rgnb->fBounds))
                    return this->setRegion(*rgna);
                break;

            case kIntersect_Op:
                if ((a_empty | b_empty)
                    || !bounds.intersect(rgna->fBounds, rgnb->fBounds))
                    return this->setEmpty();
                if (a_rect & b_rect)
                    return this->setRect(bounds);
                break;

            case kUnion_Op:
                if (a_empty)
                    return this->setRegion(*rgnb);
                if (b_empty)
                    return this->setRegion(*rgna);
                if (a_rect && rgna->fBounds.contains(rgnb->fBounds))
                    return this->setRegion(*rgna);
                if (b_rect && rgnb->fBounds.contains(rgna->fBounds))
                    return this->setRegion(*rgnb);
                break;

            case kXOR_Op:
                if (a_empty)
                    return this->setRegion(*rgnb);
                if (b_empty)
                    return this->setRegion(*rgna);
                break;
            default:
                //SkDEBUGFAIL("unknown region op");
                return !this->isEmpty();
        }

        RunType tmpA[kRectRegionRuns];
        RunType tmpB[kRectRegionRuns];

        int a_count, b_count;
        const RunType* a_runs = rgna->getRuns(tmpA, &a_count);
        const RunType* b_runs = rgnb->getRuns(tmpB, &b_count);

        int dstCount = compute_worst_case_count(a_count, b_count);
        SkAutoSTMalloc<32, SkRegion::RunType> array(dstCount);

        int count = SkRegion_operate(a_runs, b_runs, array.get(), op);
        SkASSERT(count <= dstCount);
        return this->setRuns(array.get(), count);
    }

    bool SkRegion::setRegion(const SkRegion& src) {
        if (this != &src) {
            this->freeRuns();

            fBounds = src.fBounds;
            fRunHead = src.fRunHead;
            if (fRunHead->isComplex()) {
                sk_atomic_inc(&fRunHead->fRefCnt);
            }
        }
        return fRunHead != SkRegion_gEmptyRunHeadPtr;
    }

////////////////////////////////////////////////////////
// class SkPixelRef
////////////////////////////////////////////////////////
    void SkPixelRef::notifyPixelsChanged() {
        // this signals us to recompute this next time around
        fGenerationID = 0;
    }

    bool SkPixelRef::lockPixelsAreWritable() const {
        return this->onLockPixelsAreWritable();
    }

    bool SkPixelRef::onLockPixelsAreWritable() const {
        return true;
    }

////////////////////////////////////////////////////////
// class SkBitmap
////////////////////////////////////////////////////////
    void SkBitmap::notifyPixelsChanged() const {
        SkASSERT(!this->isImmutable());
        if (fPixelRef) {
            fPixelRef->notifyPixelsChanged();
        } else {
            fRawPixelGenerationID = 0; // will grab next ID in getGenerationID
        }
    }

    bool SkBitmap::lockPixelsAreWritable() const {
        if (fPixelRef) {
            return fPixelRef->lockPixelsAreWritable();
        } else {
            return fPixels != NULL;
        }
    }

////////////////////////////////////////////////////////
// class SkDevice
////////////////////////////////////////////////////////
    void SkDevice::setMatrixClip(const SkMatrix& matrix, const SkRegion& region,
                                 const SkClipStack& clipStack) {
    }
    const SkBitmap &SkDevice::onAccessBitmap(SkBitmap *bitmap) { return *bitmap; }

    const SkBitmap &SkDevice::accessBitmap(bool changePixels) {
        const SkBitmap &bitmap = this->onAccessBitmap(&fBitmap);
        if (changePixels) {
            bitmap.notifyPixelsChanged();
        }
        return bitmap;
    }


////////////////////////////////////////////////////////
// class SkDeque
////////////////////////////////////////////////////////


    struct SkDeque::Head {
        Head *fNext;
        Head *fPrev;
        char *fBegin; // start of used section in this chunk
        char *fEnd;   // end of used section in this chunk
        char *fStop;  // end of the allocated chunk

        char *start() { return (char *) (this + 1); }

        const char *start() const { return (const char *) (this + 1); }

        void init(size_t size) {
            fNext = fPrev = NULL;
            fBegin = fEnd = NULL;
            fStop = (char *) this + size;
        }
    };


    SkDeque::F2BIter::F2BIter() : fHead(NULL), fPos(NULL), fElemSize(0) { }

    SkDeque::F2BIter::F2BIter(const SkDeque &d) {
        this->reset(d);
    }

    void *SkDeque::F2BIter::next() {
        char *pos = fPos;

        if (pos) {   // if we were valid, try to move to the next setting
            char *next = pos + fElemSize;
            SkASSERT(next <= fHead->fEnd);
            if (next == fHead->fEnd) { // exhausted this chunk, move to next
                do {
                    fHead = fHead->fNext;
                } while (fHead != NULL && fHead->fBegin == NULL);
                next = fHead ? fHead->fBegin : NULL;
            }
            fPos = next;
        }
        return pos;
    }

    void SkDeque::F2BIter::reset(const SkDeque &d) {
        fElemSize = d.fElemSize;
        fHead = d.fFront;
        while (fHead != NULL && fHead->fBegin == NULL) {
            fHead = fHead->fNext;
        }
        fPos = fHead ? fHead->fBegin : NULL;
    }

////////////////////////////////////////////////////////
// class SkAAClip
////////////////////////////////////////////////////////
    struct SkAAClip::YOffset {
        int32_t  fY;
        uint32_t fOffset;
    };

    struct SkAAClip::RunHead {
        int32_t fRefCnt;
        int32_t fRowCount;
        int32_t fDataSize;

        YOffset* yoffsets() {
            return (SkAAClip::YOffset*)((char*)this + sizeof(RunHead));
        }
        const YOffset* yoffsets() const {
            return (const YOffset*)((const char*)this + sizeof(RunHead));
        }
        uint8_t* data() {
            return (uint8_t*)(this->yoffsets() + fRowCount);
        }
        const uint8_t* data() const {
            return (const uint8_t*)(this->yoffsets() + fRowCount);
        }

        static RunHead* Alloc(int rowCount, size_t dataSize) {
            size_t size = sizeof(RunHead) + rowCount * sizeof(YOffset) + dataSize;
            RunHead* head = (RunHead*)sk_malloc(size);
            head->fRefCnt = 1;
            head->fRowCount = rowCount;
            head->fDataSize = dataSize;
            return head;
        }

        static int ComputeRowSizeForWidth(int width) {
            // 2 bytes per segment, where each segment can store up to 255 for count
            int segments = 0;
            while (width > 0) {
                segments += 1;
                int n = SkMin32(width, 255);
                width -= n;
            }
            return segments * 2;    // each segment is row[0] + row[1] (n + alpha)
        }

        static RunHead* AllocRect(const SkIRect& bounds) {
            SkASSERT(!bounds.isEmpty());
            int width = bounds.width();
            size_t rowSize = ComputeRowSizeForWidth(width);
            RunHead* head = RunHead::Alloc(1, rowSize);
            YOffset* yoff = head->yoffsets();
            yoff->fY = bounds.height() - 1;
            yoff->fOffset = 0;
            uint8_t* row = head->data();
            while (width > 0) {
                int n = SkMin32(width, 255);
                row[0] = n;
                row[1] = 0xFF;
                width -= n;
                row += 2;
            }
            return head;
        }
    };

    void SkAAClip::freeRuns() {
        if (fRunHead) {
            SkASSERT(fRunHead->fRefCnt >= 1);
            if (1 == sk_atomic_dec(&fRunHead->fRefCnt)) {
                sk_free(fRunHead);
            }
        }
    }
    bool SkAAClip::translate(int dx, int dy, SkAAClip* dst) const {
        if (NULL == dst) {
            return !this->isEmpty();
        }

        if (this->isEmpty()) {
            return dst->setEmpty();
        }

        if (this != dst) {
            sk_atomic_inc(&fRunHead->fRefCnt);
            dst->fRunHead = fRunHead;
            dst->fBounds = fBounds;
        }
        dst->fBounds.offset(dx, dy);
        return true;
    }

    bool SkAAClip::op(const SkAAClip& clipAOrig, const SkAAClip& clipBOrig,
            SkRegion::Op op) {
        //AUTO_AACLIP_VALIDATE(*this);
    #if 1 //todo:gqg
        SK_CRASH();
        return false;
    #else
        if (SkRegion::kReplace_Op == op) {
            return this->set(clipBOrig);
        }

        const SkAAClip* clipA = &clipAOrig;
        const SkAAClip* clipB = &clipBOrig;

        if (SkRegion::kReverseDifference_Op == op) {
            SkTSwap(clipA, clipB);
            op = SkRegion::kDifference_Op;
        }

        bool a_empty = clipA->isEmpty();
        bool b_empty = clipB->isEmpty();

        SkIRect bounds;
        switch (op) {
            case SkRegion::kDifference_Op:
                if (a_empty) {
                    return this->setEmpty();
                }
                if (b_empty || !SkIRect::Intersects(clipA->fBounds, clipB->fBounds)) {
                    return this->set(*clipA);
                }
                bounds = clipA->fBounds;
                break;

            case SkRegion::kIntersect_Op:
                if ((a_empty | b_empty) || !bounds.intersect(clipA->fBounds,
                                                             clipB->fBounds)) {
                    return this->setEmpty();
                }
                break;

            case SkRegion::kUnion_Op:
            case SkRegion::kXOR_Op:
                if (a_empty) {
                    return this->set(*clipB);
                }
                if (b_empty) {
                    return this->set(*clipA);
                }
                bounds = clipA->fBounds;
                bounds.join(clipB->fBounds);
                break;

            default:
                //SkDEBUGFAIL("unknown region op");
                return !this->isEmpty();
        }

        SkASSERT(SkIRect::Intersects(bounds, clipB->fBounds));
        SkASSERT(SkIRect::Intersects(bounds, clipB->fBounds));

        Builder builder(bounds);
        operateY(builder, *clipA, *clipB, op);

        return builder.finish(this);
    #endif
    }

////////////////////////////////////////////////////////
// class SkCanvas
////////////////////////////////////////////////////////


    class SkRasterClip {
    public:
        const SkRegion &forceGetBW() {
            if (!fIsBW) {
                fBW.setRect(fAA.getBounds());
            }
            return fBW;
        }

        bool isEmpty() const {
            return fIsBW ? fBW.isEmpty() : fAA.isEmpty();
        }
        void translate(int dx, int dy, SkRasterClip* dst) const {
            if (NULL == dst) {
                return;
            }

            //AUTO_RASTERCLIP_VALIDATE(*this);

            if (this->isEmpty()) {
                dst->setEmpty();
                return;
            }
            if (0 == (dx | dy)) {
                *dst = *this;
                return;
            }

            dst->fIsBW = fIsBW;
            if (fIsBW) {
                fBW.translate(dx, dy, &dst->fBW);
                dst->fAA.setEmpty();
            } else {
                fAA.translate(dx, dy, &dst->fAA);
                dst->fBW.setEmpty();
            }
        }
        bool setEmpty() {
            //AUTO_RASTERCLIP_VALIDATE(*this);

            fIsBW = true;
            fBW.setEmpty();
            fAA.setEmpty();
            return false;
        }
        bool op(const SkIRect& rect, SkRegion::Op op) {
            //AUTO_RASTERCLIP_VALIDATE(*this);
#if 1 //todo:gqg
            if (fIsBW) {
                return fBW.op(rect, op);
            } else {
                SK_CRASH();
                //return fAA.op(rect, op);
            }
            return false;
#else
            return fIsBW ? fBW.op(rect, op) : fAA.op(rect, op);
#endif
        }
    private:
        SkRegion fBW;
        SkAAClip fAA;
        bool fIsBW;

    };


    struct DeviceCM {
        DeviceCM *fNext;
        SkDevice *fDevice;
        SkRasterClip fClip;
        const SkMatrix *fMatrix;
        SkPaint *fPaint; // may be null (in the future)
        // optional, related to canvas' external matrix
        const SkMatrix *fMVMatrix;
        const SkMatrix *fExtMatrix;

        void updateMC(const SkMatrix& totalMatrix, const SkRasterClip& totalClip,
                      const SkClipStack& clipStack, SkRasterClip* updateClip) {
            const SkIPoint& pt = fDevice->getOrigin();
            int x = pt.x(); //fDevice->getOrigin().x();
            int y = pt.y(); //fDevice->getOrigin().y();
            int width = fDevice->width();
            int height = fDevice->height();

            if ((x | y) == 0) {
                fMatrix = &totalMatrix;
                fClip = totalClip;
            } else {
                fMatrixStorage = totalMatrix;
                fMatrixStorage.postTranslate(SkIntToScalar(-x),
                                             SkIntToScalar(-y));
                fMatrix = &fMatrixStorage;

                totalClip.translate(-x, -y, &fClip);
            }

            fClip.op(SkIRect::MakeWH(width, height), SkRegion::kIntersect_Op);

            // intersect clip, but don't translate it (yet)

            if (updateClip) {
                updateClip->op(SkIRect::MakeXYWH(x, y, width, height),
                               SkRegion::kDifference_Op);
            }

            fDevice->setMatrixClip(*fMatrix, fClip.forceGetBW(), clipStack);

            // default is to assume no external matrix
            fMVMatrix = NULL;
            fExtMatrix = NULL;
        }

        // can only be called after calling updateMC()
        void updateExternalMatrix(const SkMatrix& extM, const SkMatrix& extI) {
            fMVMatrixStorage.setConcat(extI, *fMatrix);
            fMVMatrix = &fMVMatrixStorage;
            fExtMatrix = &extM; // assumes extM has long life-time (owned by canvas)
        }
    private:
        SkMatrix fMatrixStorage, fMVMatrixStorage;
    };


    class SkDrawFilter;

    class SkCanvas::MCRec {
    public:
        MCRec *fNext;
        SkMatrix *fMatrix;        // points to either fMatrixStorage or prev MCRec
        SkRasterClip *fRasterClip;    // points to either fRegionStorage or prev MCRec
        SkDrawFilter *fFilter;        // the current filter (or null)

        DeviceCM *fLayer;
        /*  If there are any layers in the stack, this points to the top-most
            one that is at or below this level in the stack (so we know what
            bitmap/device to draw into from this level. This value is NOT
            reference counted, since the real owner is either our fLayer field,
            or a previous one in a lower level.)
        */
        DeviceCM *fTopLayer;


    private:
        SkMatrix fMatrixStorage;
        SkRasterClip fRasterClipStorage;
    };


    class SkDrawIter : public SkDraw {
    public:
        SkDrawIter(SkCanvas *canvas, bool skipEmptyClips = true) {
            canvas = canvas->canvasForDrawIter();
            fCanvas = canvas;
            canvas->updateDeviceCMCache();

            fClipStack = &canvas->getTotalClipStack();
            fBounder = canvas->getBounder();
            fCurrLayer = canvas->fMCRec->fTopLayer;
            fSkipEmptyClips = skipEmptyClips;
        }

        void gqg_init(SkCanvas *canvas, bool skipEmptyClips = true) {
            sk_bzero(this, sizeof(*this));
            canvas = canvas->canvasForDrawIter();
            fCanvas = canvas;
            canvas->updateDeviceCMCache();

            fClipStack = &canvas->getTotalClipStack();
            fBounder = canvas->getBounder();
            fCurrLayer = canvas->fMCRec->fTopLayer;
            fSkipEmptyClips = skipEmptyClips;
        }


        bool next() {
            // skip over recs with empty clips
            if (fSkipEmptyClips) {
                while (fCurrLayer && fCurrLayer->fClip.isEmpty()) {
                    fCurrLayer = fCurrLayer->fNext;
                }
            }

            const DeviceCM *rec = fCurrLayer;
            if (rec && rec->fDevice) {

                fMatrix = rec->fMatrix;
                fClip = &((SkRasterClip *) &rec->fClip)->forceGetBW();
                fRC = &rec->fClip;
                fDevice = rec->fDevice;
                fBitmap = &fDevice->accessBitmap(true);
                fPaint = rec->fPaint;
                fMVMatrix = rec->fMVMatrix;
                fExtMatrix = rec->fExtMatrix;
                SkDEBUGCODE(this->validate();)

                fCurrLayer = rec->fNext;
                if (fBounder) {
                    fBounder->setClip(fClip);
                }
                // fCurrLayer may be NULL now

                fCanvas->prepareForDeviceDraw(fDevice, *fMatrix, *fClip, *fClipStack);
                return true;
            }
            return false;
        }

        SkDevice *getDevice() const { return fDevice; }

        int32_t getX() const { return fDevice->getOrigin().x(); }

        int32_t getY() const { return fDevice->getOrigin().y(); }

        const SkMatrix &getMatrix() const { return *fMatrix; }

        const SkRegion &getClip() const { return *fClip; }

        const SkPaint *getPaint() const { return fPaint; }

    private:
        SkCanvas *fCanvas;
        const DeviceCM *fCurrLayer;
        const SkPaint *fPaint;     // May be null.
        SkBool8 fSkipEmptyClips;

        typedef SkDraw INHERITED;
    };

    SkCanvas::LayerIter::LayerIter(SkCanvas *canvas, bool skipEmptyClips) {
        //SK_COMPILE_ASSERT(sizeof(fStorage) >= sizeof(SkDrawIter), fStorage_too_small);

        SkASSERT(canvas);

        //todo:gqg ???
#if 0
        fImpl = new (fStorage) SkDrawIter(canvas, skipEmptyClips);
#else
        fImpl = (SkDrawIter *) fStorage;
        fImpl->gqg_init(canvas, skipEmptyClips);
#endif

        fDone = !fImpl->next();
    }

    SkCanvas::LayerIter::~LayerIter() {
        fImpl->~SkDrawIter();
    }

    void SkCanvas::LayerIter::next() {
        fDone = !fImpl->next();
    }

    SkDevice *SkCanvas::LayerIter::device() const {
        return fImpl->getDevice();
    }

    const SkMatrix &SkCanvas::LayerIter::matrix() const {
        return fImpl->getMatrix();
    }

    const SkPaint &SkCanvas::LayerIter::paint() const {
        const SkPaint *paint = fImpl->getPaint();
        if (NULL == paint) {
            paint = &fDefaultPaint;
        }
        return *paint;
    }

    const SkRegion &SkCanvas::LayerIter::clip() const { return fImpl->getClip(); }

    int32_t SkCanvas::LayerIter::x() const { return fImpl->getX(); }

    int32_t SkCanvas::LayerIter::y() const { return fImpl->getY(); }


    SkDevice *SkCanvas::getDevice() const {
        // return root device
        SkDeque::F2BIter iter(fMCStack);
        MCRec *rec = (MCRec *) iter.next();
        SkASSERT(rec && rec->fLayer);
        return rec->fLayer->fDevice;
    }

    SkISize SkCanvas::getDeviceSize() const {
        SkDevice *d = this->getDevice();
        return d ? SkISize::Make(d->width(), d->height()) : SkISize::Make(0, 0);
    }

    const SkMatrix &SkCanvas::getTotalMatrix() const {
        return *fMCRec->fMatrix;
    }

    const SkRegion &SkCanvas::getTotalClip() const {
        return fMCRec->fRasterClip->forceGetBW();
    }

    void SkCanvas::prepareForDeviceDraw(SkDevice *device, const SkMatrix &matrix,
                                        const SkRegion &clip,
                                        const SkClipStack &clipStack) {
        SkASSERT(device);
        if (fLastDeviceToGainFocus != device) {
            device->gainFocus(this, matrix, clip, clipStack);
            fLastDeviceToGainFocus = device;
        }
    }

    void SkCanvas::updateDeviceCMCache() {
        if (fDeviceCMDirty) {
            const SkMatrix& totalMatrix = this->getTotalMatrix();
            const SkRasterClip& totalClip = *fMCRec->fRasterClip;
            DeviceCM*       layer = fMCRec->fTopLayer;

            if (NULL == layer->fNext) {   // only one layer
                layer->updateMC(totalMatrix, totalClip, fClipStack, NULL);
                if (fUseExternalMatrix) {
                    layer->updateExternalMatrix(fExternalMatrix,
                                                fExternalInverse);
                }
            } else {
                SkRasterClip clip(totalClip);
                do {
                    layer->updateMC(totalMatrix, clip, fClipStack, &clip);
                    if (fUseExternalMatrix) {
                        layer->updateExternalMatrix(fExternalMatrix,
                                                    fExternalInverse);
                    }
                } while ((layer = layer->fNext) != NULL);
            }
            fDeviceCMDirty = false;
        }
    }


} // end namespace skia_api16
