//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.github.gerzytet.QuxCommands.bingo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BingoMap implements Cloneable {
    private ItemStack[][] contents = new ItemStack[5][5];
    private boolean[][] collected = new boolean[5][5];
    private static Material[][] noob;
    private static Material[][] easy;
    private static Material[][] medium;
    private static Material[][] hard;
    private static int difficulty = 2;
    private static Random randomizer = new Random();

    private static Material[][] parseLootTable(Object[] array) {
        List<Material[]> table = new ArrayList<>();
        List<Material> currentGroup = new ArrayList<>();
        boolean insideGroup = false;
        boolean endGroup = false;

        for(Object o : array) {
            String s = (String)o;
            if (s.startsWith("[")) {
                insideGroup = true;
                s = s.substring(1);
            }

            if (s.endsWith("]")) {
                endGroup = true;
                s = s.substring(0, s.length() - 1);
            }

            Material m = Material.getMaterial(s.toUpperCase());
            if (m == null) {
                System.out.println("Invalid material: " + s);
            } else {
                if (insideGroup) {
                    currentGroup.add(m);
                } else {
                    table.add(new Material[]{m});
                }

                if (endGroup) {
                    table.add((Material[])currentGroup.toArray(new Material[0]));
                    endGroup = false;
                    insideGroup = false;
                    currentGroup = new ArrayList<>();
                }
            }
        }

        return (Material[][])table.toArray(new Material[0][]);
    }

    public static void setNoobTable(Object[] array) {
        noob = parseLootTable(array);
    }

    public static void setEasyTable(Object[] array) {
        easy = parseLootTable(array);
    }

    public static void setMediumTable(Object[] array) {
        medium = parseLootTable(array);
    }

    public static void setHardTable(Object[] array) {
        hard = parseLootTable(array);
    }

    public static void setDifficulty(int d) {
        difficulty = d;
    }

    public String toString() {
        return this.contents[0][0].getType().toString();
    }

    private <T> T randomElement(T[] array) {
        return (T)array[randomizer.nextInt(array.length)];
    }

    private Material randomItem() {
        Material[][] table;
        boolean reduce = randomizer.nextInt(5) == 0;
        if (difficulty == 3) {
            boolean b = randomizer.nextBoolean();
            if (b) {
                table = hard;
            } else {
                table = medium;
            }
        }
        else if (difficulty == 1) {
            table = reduce ? noob : easy;
        } else if (difficulty == 2) {
            table = reduce ? easy : medium;
        } else {
            table = noob;
        }

        return (Material)this.randomElement((Material[])this.randomElement(table));
    }

    public void populate() {
        HashSet<Material> items = new HashSet<>();

        while(items.size() < 25) {
            items.add(this.randomItem());
        }

        int row = 0;
        int column = 0;

        for(Material mat : items) {
            this.contents[row][column] = new ItemStack(mat);
            ++row;
            if (row == 5) {
                row = 0;
                ++column;
            }
        }

    }

    public ItemStack getItem(int row, int column) {
        return this.contents[row][column];
    }

    public void setItem(int row, int column, ItemStack item) {
        this.contents[row][column] = item;
    }

    public boolean hasItem(ItemStack item) {
        for(int row = 0; row < this.contents.length; ++row) {
            for(int column = 0; column < this.contents.length; ++column) {
                if (this.contents[row][column].getType() == item.getType()) {
                    return true;
                }
            }
        }

        return false;
    }

    public int[] findItem(ItemStack item) {
        int[] result = new int[]{-1, -1};

        for(int row = 0; row < this.contents.length; ++row) {
            for(int column = 0; column < this.contents.length; ++column) {
                if (this.contents[row][column].getType() == item.getType()) {
                    result[0] = row;
                    result[1] = column;
                    return result;
                }
            }
        }

        return result;
    }

    public void collect(int row, int column) {
        this.collected[row][column] = true;
    }

    public void collect(ItemStack item) {
        int[] position = this.findItem(item);
        if (position[0] != -1) {
            this.collected[position[0]][position[1]] = true;
        }

    }

    public boolean hasCollected(int row, int column) {
        return this.collected[row][column];
    }

    public boolean hasCollected(ItemStack item) {
        for(int row = 0; row < this.contents.length; ++row) {
            for(int column = 0; column < this.contents.length; ++column) {
                if (this.contents[row][column].getType() == item.getType()) {
                    return this.collected[row][column];
                }
            }
        }

        return false;
    }

    public void unCollect(int row, int column) {
        this.collected[row][column] = false;
    }

    public boolean hasWon() {
        boolean won = true;

        for(int row = 0; row < this.collected.length; ++row) {
            won = true;

            for(int column = 0; column < this.collected.length; ++column) {
                won = won && this.collected[row][column];
            }

            if (won) {
                return true;
            }
        }

        for(int row = 0; row < this.collected.length; ++row) {
            won = true;

            for(int column = 0; column < this.collected.length; ++column) {
                won = won && this.collected[column][row];
            }

            if (won) {
                return true;
            }
        }

        won = true;

        for(int i = 0; i < 5; ++i) {
            won = won && this.collected[i][i];
        }

        if (won) {
            return true;
        } else {
            won = true;

            for(int i = 0; i < 5; ++i) {
                won = won && this.collected[i][4 - i];
            }

            if (won) {
                return true;
            } else {
                return false;
            }
        }
    }

    public BingoMap clone() {
        BingoMap map = new BingoMap();

        for(int row = 0; row < this.contents.length; ++row) {
            for(int column = 0; column < this.contents.length; ++column) {
                map.setItem(row, column, this.getItem(row, column));
                if (this.hasCollected(row, column)) {
                    map.collect(row, column);
                }
            }
        }

        return map;
    }
}
