package application.model.data_objects;

import javafx.scene.image.Image;

public class LanguageOption {
    private final String languageName;
    private final String languageCode;
    private final String countryCode;
    private final Image icon;

    public LanguageOption(String language, String languageCode, String countryCode, Image icon) {
        this.languageName = language;
        this.languageCode = languageCode;
        this.countryCode = countryCode;
        this.icon = icon;
    }

    public String getName() {
        return languageName;
    }

    public Image getIcon() {
        return icon;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
