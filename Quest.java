/**
 * Class yang merepresentasikan data Quest pada sistem BurhanQuest v2.
 * Semua atribut bersifat private, diakses melalui getter.
 */
public class Quest {

    private String id;
    private String name;
    private String description;
    private int reward;
    private int bonusExp;
    private String difficulty;
    private String status;
    private String assignedWandererId;
    private int daysRequired;
    private int dayTaken;

    /**
     * Constructor Quest.
     * @param idNumber  Nomor urut quest (ID = "Q" + idNumber)
     * @param name      Nama quest
     * @param description Deskripsi quest
     * @param reward    Reward koin
     * @param bonusExp  Bonus experience point
     * @param difficulty Tingkat kesulitan ("mudah", "menengah", "sulit")
     * @param daysRequired Durasi hari yang diperlukan
     */
    public Quest(int idNumber, String name, String description,
                 int reward, int bonusExp, String difficulty, int daysRequired) {
        this.id = "Q" + idNumber;
        this.name = name;
        this.description = description;
        this.reward = reward;
        this.bonusExp = bonusExp;
        this.difficulty = difficulty.toLowerCase();
        this.daysRequired = daysRequired;
        this.status = "tersedia";
        this.assignedWandererId = "";
        this.dayTaken = 0;
    }

    // ===================== Getters =====================

    /** @return ID quest (format "Q" + nomor) */
    public String getId() { return id; }

    /** @return Nama quest */
    public String getName() { return name; }

    /** @return Deskripsi quest */
    public String getDescription() { return description; }

    /** @return Reward koin quest */
    public int getReward() { return reward; }

    /** @return Bonus exp quest */
    public int getBonusExp() { return bonusExp; }

    /** @return Tingkat kesulitan quest */
    public String getDifficulty() { return difficulty; }

    /** @return Status quest ("tersedia", "diambil", "selesai") */
    public String getStatus() { return status; }

    /** @return ID pengembara yang mengambil quest */
    public String getAssignedWandererId() { return assignedWandererId; }

    /** @return Durasi hari yang diperlukan */
    public int getDaysRequired() { return daysRequired; }

    /** @return Hari saat quest diambil */
    public int getDayTaken() { return dayTaken; }

    // ===================== Status Checks =====================

    /** @return true jika status == "tersedia" */
    public boolean isAvailable() { return status.equals("tersedia"); }

    /** @return true jika status == "diambil" */
    public boolean isTaken() { return status.equals("diambil"); }

    /** @return true jika status == "selesai" */
    public boolean isCompleted() { return status.equals("selesai"); }

    // ===================== Methods =====================

    /**
     * Meng-assign quest ke pengembara pada hari tertentu.
     * @param wandererId ID pengembara
     * @param currentDay Hari saat quest diambil
     */
    public void assignTo(String wandererId, int currentDay) {
        this.assignedWandererId = wandererId;
        this.dayTaken = currentDay;
        this.status = "diambil";
    }

    /** Menyelesaikan quest (set status = "selesai"). */
    public void complete() {
        this.status = "selesai";
    }

    /**
     * Mengembalikan status untuk ditampilkan.
     * Jika diambil: "diambil-" + assignedWandererId, selain itu: status biasa.
     * @return String status tampilan
     */
    public String getDisplayStatus() {
        if (isTaken()) return "diambil-" + assignedWandererId;
        return status;
    }

    /**
     * Mengembalikan informasi quest yang terformat untuk ditampilkan.
     * @return String informasi quest
     */
    public String getDisplayString() {
        return "ID Quest: " + id + "\n"
             + "Nama Quest: " + name + "\n"
             + "Deskripsi Quest: " + description + "\n"
             + "Reward Quest: " + reward + " koin\n"
             + "Bonus Exp Quest: " + bonusExp + " poin exp\n"
             + "Tingkat Kesulitan Quest: " + difficulty + "\n"
             + "Durasi Quest: " + daysRequired + " hari\n"
             + "Status Quest: " + getDisplayStatus();
    }
}