package svenhjol.charmonium.charmony.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class DebugHelper {
    private final static Logger LOGGER = LogManager.getLogger("DebugHelper");
    private static final List<BooleanSupplier> DEBUG_CHECKS = new ArrayList<>();
    private static final List<BooleanSupplier> COMPAT_CHECKS = new ArrayList<>();

    public static void registerDebugCheck(BooleanSupplier supplier) {
        LOGGER.info("Adding debug check: " + supplier);
        DEBUG_CHECKS.add(supplier);
    }

    public static void registerCompatCheck(BooleanSupplier supplier) {
        LOGGER.info("Adding compat check: " + supplier);
        COMPAT_CHECKS.add(supplier);
    }

    public static boolean isDebugEnabled() {
        return DEBUG_CHECKS.stream().anyMatch(BooleanSupplier::getAsBoolean);
    }

    public static boolean isCompatEnabled() {
        return COMPAT_CHECKS.stream().anyMatch(BooleanSupplier::getAsBoolean);
    }
}
