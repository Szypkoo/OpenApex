package lol.apex.util.animation.api;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@SuppressWarnings("all")
@RequiredArgsConstructor
public enum Easing {
    LINEAR(x -> x),
    DECELERATE(x -> 1 - ((x - 1) * (x - 1))),

    SMOOTHSTEP(x -> x * x * (3 - 2 * x)),
    SMOOTHERSTEP(x -> x * x * x * (x * (x * 6 - 15) + 10)),

    EASE_IN_QUAD(x -> x * x),
    EASE_IN_CUBIC(x -> x * x * x),

    EASE_OUT_QUAD(x -> 1 - (1 - x) * (1 - x)),
    EASE_OUT_CUBIC(x -> 1 - (float) Math.pow(1 - x, 3)),
    EASE_OUT_BACK(x -> {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(x - 1, 3) + c1 * (float) Math.pow(x - 1, 2);
    }),
    EASE_OUT_ELASTIC(x -> {
        if (x == 0 || x == 1) return x;
        float c4 = (2 * (float) Math.PI) / 3;
        return (float) (Math.pow(2, -10 * x) *
                Math.sin((x * 10 - 0.75f) * c4) + 1);
    }),
    EASE_OUT_BOUNCE(x -> {
        float n1 = 7.5625f;
        float d1 = 2.75f;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            x -= 1.5f / d1;
            return n1 * x * x + 0.75f;
        } else if (x < 2.5 / d1) {
            x -= 2.25f / d1;
            return n1 * x * x + 0.9375f;
        } else {
            x -= 2.625f / d1;
            return n1 * x * x + 0.984375f;
        }
    }),

    EASE_IN_OUT_QUAD(x -> x < 0.5f
            ? 2 * x * x
            : 1 - (float) Math.pow(-2 * x + 2, 2) / 2),
    EASE_IN_OUT_CUBIC(x -> x < 0.5f
            ? 4 * x * x * x
            : 1 - (float) Math.pow(-2 * x + 2, 3) / 2);

    private final Function<Float, Float> function;

    public Function<Float, Float> getFunction() {
        return function;
    }
}