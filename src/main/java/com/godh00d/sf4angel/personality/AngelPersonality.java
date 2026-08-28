package com.godh00d.sf4angel.personality;

import java.util.*;

public class AngelPersonality {

    private static final Random RANDOM = new Random();
    private static final Map<String, Integer> LAST_SELECTIONS = new HashMap<>();

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
        "You have not visited the Nether yet. Brave or foolish?",
        "A tidy base is a tidy mind. Yours has several unsorted chests.",
        "The void has no weather. Somehow you still look underprepared.",
        "Machines are just chores persuaded to perform themselves.",
        "I counted your unfinished projects. Then I ran out of patience.",
        "Your platform is larger today. The void seems mildly offended.",
        "Somewhere, a resource tree is waiting to become infrastructure.",
        "You call it temporary storage. The chests know better.",
        "Progress is measured in milestones and misplaced crafting tables.",
        "I admire your confidence. Your wiring concerns me.",
        "The halo is decorative. The judgment is standard equipment.",
        "You have automated abundance and still carry a wooden tool.",
        "Every grand factory begins with one machine facing the wrong way.",
        "The sky is quiet. Your generators are making up for it.",
        "I have seen cleaner cable runs in bowls of noodles.",
        "Build boldly. Label your storage more boldly."
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
        "Death is but a doorway. Walk through it again.",
        "Welcome back. Gravity remains undefeated.",
        "You found the respawn button without a map. Promising.",
        "Your survival plan has entered its revision phase.",
        "The good news: you are alive. The bad news remembers you.",
        "I kept watch. Your items attempted no escape.",
        "A tactical respawn, I assume. Very tactical.",
        "You have returned from death with valuable negative experience.",
        "The sky rejected your application for permanent departure.",
        "Breathe first. Explain the explosion later.",
        "Another lesson delivered at terminal velocity.",
        "You respawned. Let us call that resilience, not repetition.",
        "Your next life begins now. Spend it less dramatically."
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
        "The sky judged you for that. It was not impressed.",
        "That was your warning tap. Mine arrives with thunder.",
        "Violence against the tutorial is rarely a winning strategy.",
        "I guide, I glow, and I retaliate. Read the full description.",
        "Your fist has made a compelling argument for lightning.",
        "You tested my patience. The test results are electrifying.",
        "I am watching you more closely now. Blink carefully.",
        "Bold move. Poor judgment. Excellent conductivity.",
        "Please keep your hands inside your own hitbox.",
        "I felt that. So did the atmosphere.",
        "Attacking your guide does not unlock a secret ending."
    };

    private static final String[] DEPARTURE_LINES = {
        "I must go. The sky calls.",
        "Until next time, builder.",
        "The angel departs. Remember what I taught you.",
        "Fly well. Build wisely.",
        "I leave you to the void. And the bonsai pots.",
        "The sky watches. I watch the sky. Goodbye.",
        "My work here is done. Yours is not.",
        "I will return when progress gives me an excuse.",
        "Keep building. I have an entrance to rehearse.",
        "Try not to reorganize everything while I am gone.",
        "The void gets the room. You get the responsibility.",
        "I leave before your next machine starts making that noise.",
        "Onward, builder. Preferably away from the platform edge.",
        "Call it a farewell. I call it dramatic repositioning.",
        "Your next milestone already has my attention.",
        "I vanish now. The judgment remains."
    };

    private static final String[] HEALTH_WARNINGS = {
        "You are dying. I can see it.",
        "Your health is low. Even I cannot fix stupid.",
        "The void is hungry. Do not feed it yourself.",
        "Eat something. Please. I beg you.",
        "You are one hit from respawn. Act accordingly.",
        "I sense your mortality. It is... close.",
        "The sky weeps for your low health.",
        "Your hearts are becoming a limited-edition collection.",
        "Heal now. Heroics are cheaper with a full health bar.",
        "You are flashing red in every plan I can foresee.",
        "Food restores health. Pride remains nutritionally useless.",
        "Your pulse sounds like a machine missing a component.",
        "Retreat is just survival with better timing.",
        "One more bad decision and I will be giving the respawn speech.",
        "Please stop treating half a heart as a challenge mode.",
        "Your armor cannot protect you from ignoring your health bar."
    };

    private static final String[] FIRST_LOGIN_INTRO = {
        "The Angel descends...",
        "I am your guide through the sky.",
        "{NEXT_GOAL}",
        "Right-click me to enter the Achievement Constellation and see every path.",
        "Complete achievements and I will show you what to do next."
    };

    private static final String[] WELCOME_BACK = {
        "Welcome back. The sky missed you.",
        "There you are. The void was getting suspiciously quiet.",
        "Welcome back, builder. Your machines deny making that noise.",
        "You return! The platform remains mostly where you left it.",
        "Back among the clouds? Good. They needed supervision.",
        "Welcome home. I have preserved all pending judgment.",
        "The sky remembers you. Your storage system filed a complaint.",
        "You are back. Let us resume making the impossible routine.",
        "Another session begins. The void has prepared no objections.",
        "Welcome back. Your unfinished projects formed a committee.",
        "The builder returns, and suddenly every machine looks busy.",
        "You logged in. Somewhere, a bonsai pot started working harder."
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
        return pick("smallTalk", SMALL_TALK);
    }

    public static String getRandomDeathLine() {
        return pick("death", DEATH_LINES);
    }

    public static String getRandomAttackResponse() {
        return pick("attack", ATTACK_RESPONSES);
    }

    public static String getRandomDepartureLine() {
        return pick("departure", DEPARTURE_LINES);
    }

    public static String getRandomHealthWarning() {
        return pick("health", HEALTH_WARNINGS);
    }

    public static String[] getFirstLoginIntro() {
        return FIRST_LOGIN_INTRO;
    }

    public static String getRandomWelcomeBack() {
        return pick("welcomeBack", WELCOME_BACK);
    }

    public static String getRandomAgeComment() {
        return pick("age", AGE_COMMENTS);
    }

    private static synchronized String pick(String category, String[] lines) {
        int selected = RANDOM.nextInt(lines.length);
        Integer previous = LAST_SELECTIONS.get(category);
        if (previous != null && selected == previous && lines.length > 1) {
            selected = (selected + 1 + RANDOM.nextInt(lines.length - 1)) % lines.length;
        }
        LAST_SELECTIONS.put(category, selected);
        return lines[selected];
    }

}
