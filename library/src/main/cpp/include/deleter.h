#ifndef STRETCH_DELETER_H
#define STRETCH_DELETER_H

#include "signalsmith-stretch.h"

struct stretch_deleter {
    void operator()(signalsmith::stretch::SignalsmithStretch<float> *stretch) { stretch->reset(); }
};

typedef std::unique_ptr<signalsmith::stretch::SignalsmithStretch<float>, stretch_deleter> stretch_ptr;

#endif //STRETCH_DELETER_H
