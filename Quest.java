// class utk nyimpen data quest
public class Quest {

    private String id;           // format "Q1", "Q2", dst
    private String name;
    private String description;
    private int reward;          // reward koin
    private int bonusExp;        // bonus exp yang didapet
    private String difficulty;   // mudah / menengah / sulit
    private String status;       // tersedia / diambil / selesai
    private String assignedWandererId; // id pengembara yang lagi ngambil quest ini
    private int daysRequired;    // butuh berapa hari buat selesain
    private int dayTaken;        // hari berapa quest ini diambil

    // constructor, bikin quest baru
    public Quest(int idNumber, String name, String description,
                 int reward, int bonusExp, String difficulty, int daysRequired) {
        this.id = "Q" + idNumber;
        this.name = name;
        this.description = description;
        this.reward = reward;
        this.bonusExp = bonusExp;
        this.difficulty = difficulty.toLowerCase();
        this.daysRequired = daysRequired;
        this.status = "tersedia"; // default pas pertama dibuat
        this.assignedWandererId = "";
        this.dayTaken = 0;
    }

    // --- getters ---

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getReward() { return reward; }
    public int getBonusExp() { return bonusExp; }
    public String getDifficulty() { return difficulty; }
    public String getStatus() { return status; }
    public String getAssignedWandererId() { return assignedWandererId; }
    public int getDaysRequired() { return daysRequired; }
    public int getDayTaken() { return dayTaken; }

    // --- ngecek status quest ---

    // true kalau quest belum diambil siapapun
    public boolean isAvailable() { return status.equals("tersedia"); }

    // true kalau quest lagi dikerjain seseorang
    public boolean isTaken() { return status.equals("diambil"); }

    // true kalau quest udah kelar
    public boolean isCompleted() { return status.equals("selesai"); }

    // assign quest ke pengembara tertentu, catat juga hari pengambilannya
    public void assignTo(String wandererId, int currentDay) {
        this.assignedWandererId = wandererId;
        this.dayTaken = currentDay;
        this.status = "diambil";
    }

    // tandain quest ini udah selesai
    public void complete() {
        this.status = "selesai";
    }

    // kalau diambil tampilin "diambil-P1" atau apapun id nya, selain itu tampilin status biasa
    public String getDisplayStatus() {
        if (isTaken()) return "diambil-" + assignedWandererId;
        return status;
    }

    // format string buat nampilin info quest
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