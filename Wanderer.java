/**
 * Class yang merepresentasikan data Pengembara (Wanderer) pada BurhanQuest v2.
 * Semua atribut bersifat private. Level pengembara selalu dimulai dari 1.
 */
public class Wanderer {

    /** Batas maksimum exp pengembara. */
    private static final int MAX_EXP = 1_310_720_000;

    private String id;
    private String name;
    private String username;
    private String password;
    private int level;
    private int exp;
    private int coins;
    private String status;

    /**
     * Constructor Wanderer.
     * @param idNumber Nomor urut pengembara (ID = "P" + idNumber)
     * @param name     Nama pengembara
     * @param username Username untuk login
     * @param password Password untuk login
     */
    public Wanderer(int idNumber, String name, String username, String password) {
        this.id = "P" + idNumber;
        this.name = name;
        this.username = username;
        this.password = password;
        this.level = 1;
        this.exp = 0;
        this.coins = 0;
        this.status = "kosong";
    }

    // ===================== Getters =====================

    /** @return ID pengembara (format "P" + nomor) */
    public String getId() { return id; }

    /** @return Nama pengembara */
    public String getName() { return name; }

    /** @return Username pengembara */
    public String getUsername() { return username; }

    /** @return Password pengembara */
    public String getPassword() { return password; }

    /** @return Level pengembara */
    public int getLevel() { return level; }

    /** @return Exp pengembara */
    public int getExp() { return exp; }

    /** @return Jumlah koin pengembara */
    public int getCoins() { return coins; }

    /** @return Status pengembara ("kosong" atau "dalam quest") */
    public String getStatus() { return status; }

    // ===================== Status & Checks =====================

    /** @return true jika status == "kosong" */
    public boolean isAvailable() { return status.equals("kosong"); }

    /**
     * Mengecek apakah pengembara memenuhi syarat level untuk mengambil quest.
     * - "mudah": semua level boleh
     * - "menengah": minimal level 6
     * - "sulit": minimal level 16
     * @param difficulty Tingkat kesulitan quest
     * @return true jika pengembara boleh mengambil quest tersebut
     */
    public boolean canTakeQuest(String difficulty) {
        String d = difficulty.toLowerCase();
        if (d.equals("mudah")) return true;
        if (d.equals("menengah")) return level >= 6;
        if (d.equals("sulit")) return level >= 16;
        return false;
    }

    /**
     * Mengecek kecocokan username dan password.
     * @param username Username yang diinput
     * @param password Password yang diinput
     * @return true jika cocok
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // ===================== Quest Methods =====================

    /** Mengubah status pengembara menjadi "dalam quest". */
    public void startQuest() {
        this.status = "dalam quest";
    }

    /**
     * Menyelesaikan quest: menambah exp, koin, mengecek level up, dan mengubah status kembali ke "kosong".
     * @param questExp    Exp yang didapatkan dari quest
     * @param questReward Koin yang didapatkan dari quest
     * @return String berisi informasi exp, koin, dan level up
     */
    public String completeQuest(int questExp, int questReward) {
        this.exp = Math.min(this.exp + questExp, MAX_EXP);
        this.coins += questReward;

        StringBuilder sb = new StringBuilder();
        sb.append("Exp didapatkan: ").append(questExp).append("\n");
        sb.append("Total Exp: ").append(this.exp).append("\n");
        sb.append("Koin didapatkan: ").append(questReward).append("\n");
        sb.append("Total Koin: ").append(this.coins);

        // Cek level up selama level < 20 dan exp memenuhi threshold
        while (level < 20 && this.exp >= getNextLevelExp(level)) {
            level++;
            sb.append("\nLevel pengembara naik menjadi: ").append(level);
        }

        this.status = "kosong";
        return sb.toString();
    }

    // ===================== Display =====================

    /**
     * Mengembalikan informasi pengembara yang terformat untuk ditampilkan.
     * @return String informasi pengembara
     */
    public String getDisplayString() {
        return "ID Pengembara: " + id + "\n"
             + "Nama Pengembara: " + name + "\n"
             + "Username: " + username + "\n"
             + "Level Pengembara: " + level + "\n"
             + "Exp Pengembara: " + exp + " poin exp\n"
             + "Koin Didapatkan: " + coins + " koin\n"
             + "Status Pengembara: " + status;
    }

    // ===================== Recursive Level Threshold =====================

    /**
     * Menghitung threshold exp untuk naik ke level berikutnya (rekursif).
     * Base case: level 1 → 5000
     * Recursive case: 2 * getNextLevelExp(currentLevel - 1)
     * @param currentLevel Level saat ini
     * @return Jumlah exp yang dibutuhkan untuk naik ke level berikutnya
     */
    public int getNextLevelExp(int currentLevel) {
        if (currentLevel == 1) return 5000;
        return 2 * getNextLevelExp(currentLevel - 1);
    }
}