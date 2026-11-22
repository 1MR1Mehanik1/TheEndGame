package gcapi_model_animation.api.animation;

import java.util.List;

/**
 * Утилита для получения интерполированных значений из списка ключевых кадров.
 */
public class AnimationHelper {

    /**
     * Линейная интерполяция между кадрами.
     * @param keyframes список, отсортированный по времени
     * @param currentTime текущее время анимации в секундах
     * @return [x,y,z]
     */
    public static float[] getValueAt(List<Keyframe> keyframes, float currentTime) {
        if (keyframes == null || keyframes.isEmpty()) return null;
        if (keyframes.size() == 1) {
            Keyframe k = keyframes.get(0);
            return new float[]{k.x, k.y, k.z};
        }
        // Обнуление времени по длине анимации, если loop, делается вызывающим кодом.
        for (int i = 0; i < keyframes.size() - 1; i++) {
            Keyframe a = keyframes.get(i);
            Keyframe b = keyframes.get(i + 1);
            if (currentTime >= a.time && currentTime <= b.time) {
                float t = (currentTime - a.time) / (b.time - a.time);
                return new float[]{
                        lerp(a.x, b.x, t),
                        lerp(a.y, b.y, t),
                        lerp(a.z, b.z, t)
                };
            }
        }
        // после последнего кадра возвращаем последний
        Keyframe last = keyframes.get(keyframes.size() - 1);
        return new float[]{last.x, last.y, last.z};
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}