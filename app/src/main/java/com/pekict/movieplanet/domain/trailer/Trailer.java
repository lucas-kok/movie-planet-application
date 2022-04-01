package com.pekict.movieplanet.domain.trailer;

public class Trailer {
    private final String site;
    private final String type;
    private final String key;
    private final boolean official;

    public Trailer(String site, String type, String key, boolean official) {
        this.site = site;
        this.type = type;
        this.key = key;
        this.official = official;
    }

    public String getSite() {
        return site;
    }

    public String getKey() {
        return key;
    }

    public boolean getOfficial() {
        return official;
    }

    public boolean isMatchForApplication() {
        return getSite().equals("YouTube") && type.equals("Trailer") && getOfficial();
    }
}
