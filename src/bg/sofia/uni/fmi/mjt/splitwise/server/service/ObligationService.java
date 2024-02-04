package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Obligation;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ObligationService {

    Collection<Obligation> getObligationsForUser(String username) throws UserNotFoundException;

    Optional<Obligation> findObligationByUsers(User first, User second);

    void addObligation(Obligation obligation);

    void updateObligation(String payerUsername, String receiverUsername, double amount);

}
