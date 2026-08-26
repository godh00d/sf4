package com.godh00d.sf4angel.personality;

import java.util.*;

public class AngelPersonality {

    private static final Random RANDOM = new Random();

    private static final String[] SMALL_TALK = {
        "The sky is vast. You are not yet worthy of it.",
        "I have watched many builders. Most fall. Some fly.",
        "Your dirt tree is... modest. Keep going.",
        "The stars are not decorations. They are warnings.",
        "I remember when this sky was empty. Now it has you.",
        "Do not look at me. Look at what you must build.",
        "Every cobblestone is a step toward something greater.",
        "The void beneath us is patient. Do not test it.",
        "I once saw a player build a house entirely out of torches. I wept.",
        "Your inventory is a mess. I can feel it.",
        "The bonsai pots are judging you. So am I.",
        "Iron does not grow on trees. Well... not yet.",
        "I sense great potential in you. And great clumsiness.",
        "The chisel and bits await. Do not fear the micro.",
        "You have not visited the Nether yet. Brave or foolish?"
    };

    private static final String[] DEATH_LINES = {
        "The Angel reclaims what was lost...",
        "Death cannot hold you. The heavens forbid it.",
        "I watched you die. It was... educational.",
        "The sky does not forgive. But I do. This time.",
        "Your items are scattered. Retrieve them before the void does.",
        "I was not fast enough. I am sorry.",
        "Rise. You are not done yet.",
        "The void took you. I brought you back. You owe me.",
        "That was painful to watch. Let us not repeat it.",
        "Even in death, you respawn. The sky is merciful.",
        "I cannot protect you from yourself.",
        "You fell. The sky remembers every fall.",
        "Death is but a doorway. Walk through it again."
    };

    private static final String[] ATTACK_RESPONSES = {
        "Do NOT strike me. The heavens will answer.",
        "Foolish mortal. The lightning remembers.",
        "I am not your enemy. The void is.",
        "Strike me again and see what happens.",
        "I am made of light. You are made of regret.",
        "The angel does not forgive twice.",
        "You dare? ...Interesting.",
        "I expected better. I was wrong.",
        "That hurt my feelings. I am not sure I have feelings.",
        "The sky judged you for that. It was not impressed."
    };

    private static final String[] HINT_GREETINGS = {
        "Ah, you seek guidance. Wise.",
        "The angel remembers your struggles.",
        "I have been watching. You need help.",
        "Ask, and the sky shall answer.",
        "Your confusion radiates like end rods.",
        "I sense... uncertainty. Let me help.",
        "The path ahead is clear. To me."
    };

    private static final String[] SACRIFICE_REFUNDS = {
        "An offering? How thoughtful. Here is my advice instead.",
        "I do not eat. But I appreciate the gesture.",
        "Your sacrifice has been noted. And judged.",
        "The sky accepts your offering. In return, knowledge.",
        "I will remember this. The items I return are yours.",
        "Generosity. A rare trait in the sky."
    };

    private static final String[] DEPARTURE_LINES = {
        "I must go. The sky calls.",
        "Until next time, builder.",
        "The angel departs. Remember what I taught you.",
        "Fly well. Build wisely.",
        "I leave you to the void. And the bonsai pots.",
        "The sky watches. I watch the sky. Goodbye.",
        "My work here is done. Yours is not."
    };

    private static final String[] HEALTH_WARNINGS = {
        "You are dying. I can see it.",
        "Your health is low. Even I cannot fix stupid.",
        "The void is hungry. Do not feed it yourself.",
        "Eat something. Please. I beg you.",
        "You are one hit from respawn. Act accordingly.",
        "I sense your mortality. It is... close.",
        "The sky weeps for your low health."
    };

    private static final String[] FIRST_LOGIN_INTRO = {
        "The Angel descends...",
        "I am your guide through the sky.",
        "{GOAL_AND_HINT}",
        "Complete achievements and I will show you what to do next."
    };

    private static final String[] AGE_COMMENTS = {
        "You have entered a new age. The sky trembles.",
        "Another age conquered. Impressive. For a mortal.",
        "The ages bend to your will. Mostly.",
        "New age, new challenges. Try not to die.",
        "The progression continues. The angel approves.",
        "You are closer to the end. Or the end of you."
    };

    public static String getRandomSmallTalk() {
        return SMALL_TALK[RANDOM.nextInt(SMALL_TALK.length)];
    }

    public static String getRandomDeathLine() {
        return DEATH_LINES[RANDOM.nextInt(DEATH_LINES.length)];
    }

    public static String getRandomAttackResponse() {
        return ATTACK_RESPONSES[RANDOM.nextInt(ATTACK_RESPONSES.length)];
    }

    public static String getRandomHintGreeting() {
        return HINT_GREETINGS[RANDOM.nextInt(HINT_GREETINGS.length)];
    }

    public static String getRandomSacrificeRefund() {
        return SACRIFICE_REFUNDS[RANDOM.nextInt(SACRIFICE_REFUNDS.length)];
    }

    public static String getRandomDepartureLine() {
        return DEPARTURE_LINES[RANDOM.nextInt(DEPARTURE_LINES.length)];
    }

    public static String getRandomHealthWarning() {
        return HEALTH_WARNINGS[RANDOM.nextInt(HEALTH_WARNINGS.length)];
    }

    public static String[] getFirstLoginIntro() {
        return FIRST_LOGIN_INTRO;
    }

    public static String getRandomAgeComment() {
        return AGE_COMMENTS[RANDOM.nextInt(AGE_COMMENTS.length)];
    }

    public static String getAdvancementGreeting(String advancementName) {
        String[] greetings = {
            "Congratulations! You have unlocked: " + advancementName,
            "The heavens celebrate: " + advancementName,
            "Well done. " + advancementName + " is yours.",
            "The angel smiles upon: " + advancementName,
            "Another milestone: " + advancementName + ". Keep building.",
            "The sky acknowledges: " + advancementName,
            "Progress! " + advancementName + " achieved."
        };
        return greetings[RANDOM.nextInt(greetings.length)];
    }

    public static String getContributionLine(int count) {
        if (count == 1) return "A single item? The sky is generous with its patience.";
        if (count < 5) return count + " items. The angel is mildly impressed.";
        if (count < 10) return count + " items. Now you have my attention.";
        if (count < 20) return count + " items. The angel nods approvingly.";
        return count + " items. You are generous, mortal. Here is wisdom.";
    }
}
