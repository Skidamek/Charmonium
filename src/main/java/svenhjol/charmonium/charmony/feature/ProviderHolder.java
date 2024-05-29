package svenhjol.charmonium.charmony.feature;

import svenhjol.charmonium.charmony.Api;
import svenhjol.charmonium.charmony.Feature;

public abstract class ProviderHolder<F extends Feature> extends FeatureHolder<F> implements Conditional {
    public ProviderHolder(F feature) {
        super(feature);
        Api.registerProvider(this);
        feature.loader().registerConditional(this);
    }

    @Override
    public boolean isEnabled() {
        return feature().isEnabled();
    }
}
