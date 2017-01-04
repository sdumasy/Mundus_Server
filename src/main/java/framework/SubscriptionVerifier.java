package framework;

import models.Player;

/**
 * Verifies if a player is allowed to subscribe to the predefined data.
 */
public interface SubscriptionVerifier {

    /**
     * Checks whether the given player is allowed to subscribe to the predefined data of the given session.
     *
     * @param player    The player who is trying to subscribe.
     * @param sessionID The ID of the session trying to subscribe to.
     * @return Whether the player is allowed to subscribe.
     */
    boolean handle(Player player, String sessionID);
}
