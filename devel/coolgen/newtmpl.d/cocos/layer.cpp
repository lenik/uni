#include "<?= $Name ?>.h"

USING_NS_CC;

bool <?= $Name ?>::init() {
    if (!Layer::init())
        return false;
    
    Size visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();
    
    return true;
}
