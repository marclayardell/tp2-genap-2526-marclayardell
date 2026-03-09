// utk simpen data pengembara, ngecek level up, dll
public class Wanderer {

    // batas max exp, ga bisa lebih dari ini
    private static final int MAX_EXP = 1_310_720_000;

    private String id;       // format "P1", "P2", dst
    private String name;
    private String username;
    private String password;
    private int level;       // mulai dari 1
    private int exp;         // mulai dari 0
    private int coins;       // mulai dari 0
    private String status;   // kosong / dalam quest

    // constructor, semua pengembara baru mulai dari level 1, exp 0, coins 0
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

    // --- getters ---

    public String getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getCoins() { return coins; }
    public String getStatus() { return status; }

    // true kalau pengembara lagi ga ngerjain quest apapun
    public boolean isAvailable() { return status.equals("kosong"); }

    // ngecek apakah level pengembara cukup buat ngambil quest ini
    // mudah = semua level boleh, menengah = min 6, sulit = min 16
    public boolean canTakeQuest(String difficulty) {
        String d = difficulty.toLowerCase();
        if (d.equals("mudah")) return true;
        if (d.equals("menengah")) return level >= 6;
        if (d.equals("sulit")) return level >= 16;
        return false;
    }

    // ngecek username sama password, buat login
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // ubah status jadi "dalam quest" pas pengembara ngambil quest
    public void startQuest() {
        this.status = "dalam quest";
    }

    // dipanggil waktu quest selesai
    // nambahin exp dan koin, ngecek level up, terus balik status ke kosong
    public String completeQuest(int questExp, int questReward) {
        this.exp = Math.min(this.exp + questExp, MAX_EXP); // exp ga boleh melebihi MAX_EXP
        this.coins += questReward;

        StringBuilder sb = new StringBuilder();
        sb.append("Exp didapatkan: ").append(questExp).append("\n");
        sb.append("Total Exp: ").append(this.exp).append("\n");
        sb.append("Koin didapatkan: ").append(questReward).append("\n");
        sb.append("Total Koin: ").append(this.coins);

        // cek terus apakah exp udah cukup buat level up, selama belum level 20
        while (level < 20 && this.exp >= getNextLevelExp(level)) {
            level++;
            sb.append("\nLevel pengembara naik menjadi: ").append(level);
        }

        this.status = "kosong"; // quest kelar, status balik kosong
        return sb.toString();
    }

    // format string buat nampilin info pengembara
    public String getDisplayString() {
        return "ID Pengembara: " + id + "\n"
             + "Nama Pengembara: " + name + "\n"
             + "Username: " + username + "\n"
             + "Level Pengembara: " + level + "\n"
             + "Exp Pengembara: " + exp + " poin exp\n"
             + "Koin Didapatkan: " + coins + " koin\n"
             + "Status Pengembara: " + status;
    }

    // ngitung berapa exp yang dibutuhin buat naik ke level berikutnya
    // pake rekursi: level 1 butuh 5000, level berikutnya selalu 2x lipat
    public int getNextLevelExp(int currentLevel) {
        if (currentLevel == 1) return 5000; // base case
        return 2 * getNextLevelExp(currentLevel - 1); // recursive case
    }
}