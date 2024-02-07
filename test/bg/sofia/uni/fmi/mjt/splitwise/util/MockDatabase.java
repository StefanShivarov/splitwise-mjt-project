package bg.sofia.uni.fmi.mjt.splitwise.util;

public class MockDatabase {

    public static String USERS_CSV =
            """
                    stefanaki95,SnWwnrL6LR2tGJdf6Jv8R92oX982xbnJq3RwTcHRYpI=,Stefan,Shivarov
                    ivan,mvFbM25qlhmShTffMLLmojdlafz51+dz7M7eZWBlKaA=,Ivan,Ivanov
                    valio,QcmR62pmJCwEVBkSRCeBg85Yz0przTcveZ5LnMAYhq8=,,""";

    public static String FRIENDSHIPS_CSV =
            """
                    ivan,stefanaki95
                    valio,stefanaki95""";

    public static String NOTIFICATIONS_CSV =
            """
                    stefan shivarov paid 20.00 (10.00 each) for you [vodka].,ivan,2024-02-06 21:30:42,true
                    stefan shivarov added you as a friend!,ivan,2024-02-06 21:30:31,true
                    stefan shivarov paid 30.00 (10.00 each) for group diskoteka [whiskey].,valio,2024-02-06 22:58:32,true
                    stefan shivarov paid 24.00 (8.00 each) for group diskoteka [alcohol].,valio,2024-02-06 22:56:41,true""";

}
// I am going to need to refactor a lot of my code to be able to test most of the classes ,
// but unfortunately I don't have enough time left :(
