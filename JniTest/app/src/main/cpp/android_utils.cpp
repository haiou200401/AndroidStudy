#include "android_utils.h"

namespace plat_support {


static int g_android_version = 16;

void initAndroidVersion(int version) {
	g_android_version = version;
}

int getAndroidVersion() {
	return g_android_version;
}



}
