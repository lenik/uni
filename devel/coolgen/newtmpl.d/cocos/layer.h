#ifndef __<?= $NA_ME ?>_H__
#define __<?= $NA_ME ?>_H__

#include "cocos2d.h"

/**
 * The <?= $Name ?> Layer.
 */
class <?= $Name ?> : public cocos2d::Layer {
    
    int m_;

public:
    /* Declares $Name *$Name::create(). */
    CREATE_FUNC(<?= $Name ?>);

    virtual bool init();

protected:
    
private:
    
};

#endif
