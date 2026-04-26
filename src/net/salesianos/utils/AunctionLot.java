package net.salesianos.utils;

import net.salesianos.models.Item;

public class AunctionLot {
    private Item[] inventory;
    private int actualIndex;

    public AunctionLot() {
        this.inventory = Constants.MOCK_LOTS;
        this.actualIndex = 0;
    }

    public Item getOneItem() {
        if (actualIndex < inventory.length) {
            return inventory[actualIndex];
        }
        return null;
    }

    public boolean nextItemBet() {
        actualIndex++;
        return actualIndex < inventory.length;
    }
}