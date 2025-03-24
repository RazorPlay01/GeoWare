package com.github.razorplay01.geoware.geowareplugin.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Emote {
    ARM_DANCE("\"Arm dance\""),
    BACK_FLIP("\"Back Flip\""),
    BREAKDANCE_FLARE("\"Breakdance Flare\""),
    CHAOS_CHAOS("\"CHAOS CHAOS\""),
    CLUB_PENGUIN_DANCE("\"club penguin dance\""),
    CPD_BEND("\"CPD bend\""),
    DANCE_RALSEI("\"Dance (Ralsei)\""),
    DANCE_MOVES("\"Dance Moves\""),
    DEFAULT_DANCE("\"Default Dance\""),
    DEFAULT_DANCE_LOOP("\"Default Dance(loop)\""),
    FACE_PALM("\"Face palm\""),
    HAI_PHUT_HON_DANCE("\"Hai Phut Hon dance\""),
    HAKARI_DANCE_SEHEN_V2("\"Hakari Dance (Sehen) V2\""),
    HAKARI_DANCE_SEHEN("\"Hakari Dance (Sehen)\""),
    HAKARI_DANCE("\"Hakari Dance\""),
    KAZOTSKY_KICK("\"kazotsky kick\""),
    LETHAL_DANCE("\"Lethal Dance\""),
    MELON_CRAPPY("\"Melon (crappy)\""),
    OVER_HERE("\"Over here\""),
    PEPPINO_SOUND_TEST_DANCE("\"Peppino Sound Test Dance\""),
    PIGLIN_VICTORY_DANCE("\"piglin victory dance\""),
    RAT_DANCE_1_2("\"Rat Dance 1.2\""),
    ROBLOX_POTION_DANCE("\"roblox potion dance\""),
    SMUG_DANCE("\"Smug dance\""),
    SNEAKY_DANCE("\"Sneaky Dance\""),
    THAILAND_MANJUA_DANCE("\"Thailand Manjua dance\""),
    WORTH_THE_WAIT_DANCE_FULL("\"Worth the Wait Dance (full ver)\""),
    WORTH_THE_WAIT_DANCE("\"Worth the Wait Dance\""),
    ZERO_TWO_WIGGLE("\"Zero Two Wiggle\""),
    CLAP("\"Clap\""),
    CRYING("\"Crying\""),
    HAKARI("\"Hakari\""),
    KINJI_HAKARI_DANCE("\"KinjiHakariDance\""),
    POINT("\"Point\""),
    POKEDANCE("\"Pokedance\""),
    T_KL_SIMPLE_DANCE("\"T-KL-SimpleDance\""),
    WAVING("\"Waving\""),
    STOP("\"stop\"");

    private final String emoteId;

    public static Emote fromString(String emoteId) {
        for (Emote emote : values()) {
            if (emote.getEmoteId().equals(emoteId)) {
                return emote;
            }
        }
        return null;
    }
}