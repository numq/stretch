#ifndef STRETCH_DELETER_H
#define STRETCH_DELETER_H

#include "signalsmith-stretch.h"

struct SignalsmithStretchDeleter {
    void operator()(signalsmith::stretch::SignalsmithStretch<float> *stretch) const {
        if (stretch) {
            stretch->reset();
        }
    }
};

#endif //STRETCH_DELETER_H
