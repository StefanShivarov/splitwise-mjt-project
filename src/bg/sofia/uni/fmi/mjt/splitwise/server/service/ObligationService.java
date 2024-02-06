package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Obligation;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ObligationService {

    Collection<Obligation> getObligationsForUser(String username)
            throws UserNotFoundException;

    Optional<Obligation> findObligationByUsers(String firstUsername,
                                               String secondUsername)
            throws UserNotFoundException;

    Optional<Obligation> findObligationByUsers(User u1, User u2);

    void addObligation(User first, User second, double amount);

    void updateObligation(String payerUsername, String receiverUsername,
                          double updatedAmount) throws UserNotFoundException;

    void updateObligation(User payer, User receiver, double paidAmount);

    String getObligationStatusWithUserForLoggedInUser(String loggedUsername,
                                                      String otherUsername)
            throws UserNotFoundException;

}
