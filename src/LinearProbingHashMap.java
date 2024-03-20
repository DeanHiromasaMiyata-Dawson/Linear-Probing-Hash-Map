import java.util.List;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Implementation of a LinearProbingHashMap.
 */
public class LinearProbingHashMap<K, V> {

    /**
     * The initial capacity of the LinearProbingHashMap when created with the
     * default constructor.
     */
    public static final int INITIAL_CAPACITY = 13;

    /**
     * The max load factor of the LinearProbingHashMap
     */
    public static final double MAX_LOAD_FACTOR = 0.67;

    private LinearProbingMapEntry<K, V>[] table;
    private int size;

    /**
     * Constructs a new LinearProbingHashMap.
     * The backing array has initial capacity of INITIAL_CAPACITY.
     */
    public LinearProbingHashMap() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Constructs a new LinearProbingHashMap.
     * The backing array should has an initial capacity of initialCapacity.
     * initialCapacity is assumed to be always positive.
     *
     * @param initialCapacity the initial capacity of the backing array
     */
    public LinearProbingHashMap(int initialCapacity) {
        table = new LinearProbingMapEntry[initialCapacity];
        size = 0;
    }

    /**
     * Adds the given key-value pair to the map. If an entry in the map
     * already has this key, replace the entry's value with the new one
     * passed in.
     *
     * In the case of a collision, use linear probing as your resolution
     * strategy.
     *
     * Before actually adding any data to the HashMap,
     * checks if the array would violate the max load factor if the data was
     * added. For example, let's say the array is of length 5 and the current
     * size is 3 (LF = 0.6). For this example, assume that no elements are
     * removed in between steps. If another entry is attempted to be added,
     * before doing anything else, checks whether (3 + 1) / 5 = 0.8 is larger than the max LF.
     * It is, so resizes before adding the data or figure out if it's a duplicate.
     *
     * Regrowing resizes the length of the backing table to 2 * old length + 1 using the resizeBackingTable method to do so.
     *
     * Returns null if the key was not already in the map.
     * If it was in the map, returns the old value associated with it.
     *
     * @param key the key to add
     * @param value the value to add
     * @return null if the key was not already in the map. If it was in the map, return the old value associated with it
     * @throws java.lang.IllegalArgumentException if key or value is null
     */
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Error: key or value is null.");
        }

        if ((size + 1) / (double) table.length > MAX_LOAD_FACTOR) {
            resizeBackingTable(table.length * 2 + 1);
        }

        int index = Math.abs(key.hashCode() % table.length);

        if (table[index] == null) {
            table[index] = new LinearProbingMapEntry<K, V>(key, value);
        } else {
            LinearProbingMapEntry<K, V> curr = table[index];
            LinearProbingMapEntry<K, V> next = table[index + 1];

            while (next != null && !(curr.getKey().equals(key))) {
                table[index] = table[index + 1];
            }

            if (curr.getKey().equals(key)) {
                V oldValue = curr.getValue();
                curr.setValue(value);
                return oldValue;
            } else if (next != null) {
                next.setKey(key);
                next.setValue(value);
            }
        }
        size++;
        return null;
    }

    /**
     * Removes the entry with a matching key from map by marking the entry as
     * removed.
     *
     * @param key the key to remove
     * @return the value previously associated with the key
     * @throws java.lang.IllegalArgumentException if key is null
     * @throws java.util.NoSuchElementException   if the key is not in the map
     */
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Error: key is null.");
        }
        if (!(containsKey(key))) {
            throw new NoSuchElementException("Error: key is not in the map.");
        }

        int index = Math.abs(key.hashCode() % table.length);

        if (table[index] != null) {
            LinearProbingMapEntry<K, V> prev = table[index - 1];
            LinearProbingMapEntry<K, V> curr = table[index];
            LinearProbingMapEntry<K, V> next = table[index + 1];

            while (next != null && !(curr.getKey().equals(key))) {
                prev = curr;
                curr = next;
            }

            if (curr.getKey().equals(key)) {
                V removed = curr.getValue();

                if (prev== null) {
                    curr = next;
                } else if (next != null){
                    curr.setValue(next.getValue());
                }
                size--;
                return removed;
            }
        }
        return null;
    }

    /**
     * Gets the value associated with the given key.
     *
     * @param key the key to search for in the map
     * @return the value associated with the given key
     * @throws java.lang.IllegalArgumentException if key is null
     * @throws java.util.NoSuchElementException if the key is not in the map
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Error: key is null.");
        }
        if (!(containsKey(key))) {
            throw new NoSuchElementException("Error: key is not in the map.");
        }

        LinearProbingMapEntry<K, V> curr = table[Math.abs(key.hashCode() % table.length)];
        LinearProbingMapEntry<K, V> next = table[Math.abs(key.hashCode() % table.length) + 1];

        while (curr != null && !(curr.getKey().equals(key))) {
            curr = next;
        }
        if (curr != null) {
            return curr.getValue();
        }
        return null;
    }

    /**
     * Returns whether or not the key is in the map.
     *
     * @param key the key to search for in the map
     * @return true if the key is contained within the map, false
     * otherwise
     * @throws java.lang.IllegalArgumentException if key is null
     */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Error: key is null.");
        }

        return this.get(key) != null;
    }

    /**
     * Returns a Set view of the keys contained in this map using a HashSet.
     *
     * @return the set of keys in this map
     */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();

        for (int i = 0; i < size; i++) {
            LinearProbingMapEntry<K, V> curr = table[i];
            LinearProbingMapEntry<K, V> next = table[i + 1];

            if (curr != null) {
                keys.add((K) curr.getKey());

                while (next != null) {
                    curr = next;
                    keys.add((K) curr.getKey());
                }
            }
        }

        return keys;
    }

    /**
     * Returns a List view of the values contained in this map using an ArrayList.
     *
     * Iterates over the table in order of increasing index 
     * and adds entries to the List in the order in which they are traversed.
     *
     * @return list of values in this map
     */
    public List<V> values() {
        List<V> values = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            LinearProbingMapEntry<K, V> curr = table[i];
            LinearProbingMapEntry<K, V> next = table[i + 1];

            if (curr != null) {
                values.add((V) curr.getValue());

                while (next != null) {
                    curr = next;
                    values.add((V) curr.getValue());
                }
            }
        }

        return values;
    }

    /**
     * Resize the backing table to length.
     *
     * Disregard the load factor for this method. So, if the passed in length is
     * smaller than the current capacity, and this new length causes the table's
     * load factor to exceed MAX_LOAD_FACTOR, resizes the table
     * to the specified length and leaves it at that capacity.
     *
     * Iterates over the old table in order of increasing index and
     * adds entries to the new table in the order in which they are traversed.
     * Without copying over removed elements to the resized backing table.
     *
     * @param length new length of the backing table
     * @throws java.lang.IllegalArgumentException if length is less than the number of items in the hash map
     */
    public void resizeBackingTable(int length) {
        if (length <= 0 || length > size) {
            throw new IllegalArgumentException("Error: length is negative or larger than the size of this map.");
        }

        LinearProbingMapEntry<K, V>[] entries = new LinearProbingMapEntry[length];

        for (LinearProbingMapEntry<K, V> entry : table) {
            if (entry != null) {
                int index = Math.abs(entry.getKey().hashCode() % entries.length);
                entries[index] = entry;
            }
        }
        table = entries;
    }

    /**
     * Clears the map.
     *
     * Resets the table to a new array of the INITIAL_CAPACITY and resets the
     * size.
     *
     * Must be O(1).
     */
    public void clear() {
        table = new LinearProbingMapEntry[INITIAL_CAPACITY];
        size = 0;
    }

    /**
     * Returns the table of the map.
     *
     * @return the table of the map
     */
    public LinearProbingMapEntry<K, V>[] getTable() {
        return table;
    }

    /**
     * Returns the size of the map.
     *
     * @return the size of the map
     */
    public int size() {
        return size;
    }
}

