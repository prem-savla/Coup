package com.game.coup.entities;


public class Treasury {

    private int coins;

    public Treasury() {
        this.coins = 50;
    }

    public void giveCoins(Player player, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        int transfer = Math.min(amount, coins);

        if (transfer == 0) {
            throw new IllegalStateException("Treasury is empty");
        }

        player.addCoins(transfer);
        coins -= transfer;
    }

    public void takeCoins(Player player, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (coins + amount > 50) {
        throw new IllegalStateException("Treasury overflow detected.");
    }

        player.removeCoins(amount); 
        coins += amount;
    }

    public int getCoins() {
        return coins;
    }
}
