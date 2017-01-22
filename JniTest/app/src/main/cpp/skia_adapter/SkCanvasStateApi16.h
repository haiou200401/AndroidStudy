//
// Created by gaoqingguang on 2017/1/12.
//

#ifndef SK_CANVAS_STATE_API16_H__
#define SK_CANVAS_STATE_API16_H__



#include "SkCanvasState.h"


class SkCanvasStateFactoryApi16 : public SkCanvasStateFactory {
public:
    SkCanvasStateFactoryApi16() {}
    SkCanvasState* CaptureCanvasState(void* canvas) override;
    void ReleaseCanvasState(SkCanvasState* state) override;
};



#endif //SK_CANVAS_STATE_API16_H__
