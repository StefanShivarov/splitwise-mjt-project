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

}
    // I am going to need to refactor a lot of my code to be able to test most of the classes ,
    // but unfortunately I don't have enough time left :(
