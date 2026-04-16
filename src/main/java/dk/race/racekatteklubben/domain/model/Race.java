package dk.race.racekatteklubben.domain.model;

public enum Race {
    MAINE_COON,
    BRITISH_SHORTHAIR,
    BENGAL,
    SIAMESE,
    RAGDOLL,
    SPHYNX;

    public String getDisplayName() {
        String[] words = name().toLowerCase().split("_");
        StringBuilder displayName = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                displayName.append(' ');
            }

            String word = words[i];
            displayName.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }

        return displayName.toString();
    }
}
