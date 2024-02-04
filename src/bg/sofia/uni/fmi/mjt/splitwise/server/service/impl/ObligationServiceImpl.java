package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.ObligationCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.ObligationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Obligation;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ObligationServiceImpl implements ObligationService {

    private final ObligationCsvProcessor obligationCsvProcessor;
    private final UserService userService;
    private final Set<Obligation> obligations;

    public ObligationServiceImpl(UserService userService) {
        this.userService = userService;
        this.obligationCsvProcessor = new ObligationCsvProcessor(userService);
        this.obligations = obligationCsvProcessor.loadObligationsFromCsvFile();
    }

    @Override
    public Collection<Obligation> getObligationsForUser(String username) throws UserNotFoundException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Invalid argument! Username is null or blank!");
        }

        Optional<User> user = userService.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with username " + username + " was not found!");
        }

        return obligations
                .stream()
                .filter(obligation -> obligation.getFirstUser().equals(user.get())
                        || obligation.getSecondUser().equals(user.get()))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Obligation> findObligationByUsers(User u1, User u2) {
        return obligations
                .stream()
                .filter(obligation -> (obligation.getFirstUser().equals(u1)
                        && obligation.getSecondUser().equals(u2))
                        || (obligation.getFirstUser().equals(u2)
                        && obligation.getSecondUser().equals(u1)))
                .findFirst();
    }

    @Override
    public void addObligation(User payer, User receiver, double amount) {
        if (payer == null || receiver == null) {
            throw new IllegalArgumentException("Invalid arguments! User is null!");
        }

        Obligation obligation = new Obligation(payer, receiver, -1 * amount);
        obligations.add(obligation);
        obligationCsvProcessor.writeObligationToCsvFile(obligation);
    }

    @Override
    public void updateObligation(String payerUsername,
                                 String receiverUsername,
                                 double amountPaid) throws UserNotFoundException {
        Optional<User> payer = userService.findUserByUsername(payerUsername);
        if (payer.isEmpty()) {
            throw new UserNotFoundException("User with username " + payerUsername + " was not found!");
        }
        Optional<User> receiver = userService.findUserByUsername(receiverUsername);
        if (receiver.isEmpty()) {
            throw new UserNotFoundException("User with username " + receiverUsername + " was not found!");
        }

        updateObligation(payer.get(), receiver.get(), amountPaid);
    }

    @Override
    public void updateObligation(User payer, User receiver, double amountPaid) {
        if (payer == null || receiver == null) {
            throw new IllegalArgumentException("Invalid arguments! User is null!");
        }

        Optional<Obligation> obligation = findObligationByUsers(payer, receiver);
        if (obligation.isEmpty()) {
            addObligation(payer, receiver, amountPaid);
            return;
        }

        double updatedAmount;
        if (obligation.get().getFirstUser().equals(payer)) {
            updatedAmount = obligation.get().getBalance() - amountPaid;
        } else {
            updatedAmount = obligation.get().getBalance() + amountPaid;
        }

        obligation.get().setBalance(updatedAmount);
        try {
            obligationCsvProcessor.updateObligationInCsvFile(obligation.get());
        } catch (ObligationNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
