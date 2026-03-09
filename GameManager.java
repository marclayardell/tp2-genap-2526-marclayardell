/**
 * Class yang mengatur seluruh logika bisnis program BurhanQuest v2.
 * Menyimpan data quest dan pengembara menggunakan array biasa (bukan ArrayList/Collection).
 * Tidak menggunakan Scanner maupun System.out (I/O hanya di Main).
 */
public class GameManager {

    private static final String ADMIN_USERNAME = "burhan";
    private static final String ADMIN_PASSWORD = "burunghantu123";

    private Quest[] quests = new Quest[1000];
    private int questCount = 0;

    private Wanderer[] wanderers = new Wanderer[1000];
    private int wandererCount = 0;

    private int currentDay = 1;

    // ===================== Day System =====================

    /** @return Hari saat ini */
    public int getCurrentDay() { return currentDay; }

    /**
     * Memajukan hari ke depan dan mengecek quest yang selesai.
     * @return String yang menggambarkan peristiwa yang terjadi saat hari berganti
     */
    public String advanceDay() {
        currentDay++;
        StringBuilder sb = new StringBuilder("Hari berganti menjadi hari ke-" + currentDay + ".");

        boolean anyCompleted = false;
        for (int i = 0; i < questCount; i++) {
            if (quests[i].isTaken()
                    && currentDay >= quests[i].getDayTaken() + quests[i].getDaysRequired()) {

                String wId = quests[i].getAssignedWandererId();
                Wanderer wanderer = findWandererById(wId);
                quests[i].complete();

                if (wanderer != null) {
                    String result = wanderer.completeQuest(quests[i].getBonusExp(), quests[i].getReward());
                    sb.append("\nQuest \"").append(quests[i].getName())
                      .append("\" (").append(quests[i].getId())
                      .append(") telah diselesaikan oleh ").append(wanderer.getName())
                      .append(" (").append(wanderer.getId()).append(")!\n")
                      .append(result);
                    anyCompleted = true;
                }
            }
        }

        if (!anyCompleted) {
            sb.append("\nTidak ada quest yang selesai hari ini.");
        }
        return sb.toString();
    }

    // ===================== Authentication =====================

    /**
     * Melakukan login.
     * @param username Username yang diinput
     * @param password Password yang diinput
     * @return "admin" jika login sebagai admin, nama pengembara jika login sebagai pengembara,
     *         null jika tidak cocok
     */
    public String login(String username, String password) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            return "admin";
        }
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].authenticate(username, password)) {
                return wanderers[i].getName();
            }
        }
        return null;
    }

    /**
     * Mendapatkan ID pengembara berdasarkan username.
     * @param username Username pengembara
     * @return ID pengembara, atau null jika tidak ditemukan
     */
    public String getWandererIdByUsername(String username) {
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].getUsername().equals(username)) {
                return wanderers[i].getId();
            }
        }
        return null;
    }

    // ===================== Quest Management =====================

    /** @return Nomor quest berikutnya (untuk ditampilkan saat penambahan) */
    public int getNextQuestNumber() { return questCount + 1; }

    /**
     * Menambahkan quest baru dengan validasi input.
     * @param name         Nama quest (raw string dari input)
     * @param description  Deskripsi quest
     * @param rewardStr    Reward koin (string)
     * @param bonusExpStr  Bonus exp (string)
     * @param difficulty   Tingkat kesulitan
     * @param daysRequiredStr Durasi hari (string)
     * @return "invalid" jika input tidak valid, "success" jika berhasil
     */
    public String addQuest(String name, String description,
                           String rewardStr, String bonusExpStr,
                           String difficulty, String daysRequiredStr) {
        if (!isValidQuestTextField(name)
                || !isValidQuestTextField(description)
                || !isValidNonNegativeInt(rewardStr)
                || !isValidNonNegativeInt(bonusExpStr)
                || !isValidDifficulty(difficulty)
                || !isValidPositiveInt(daysRequiredStr)) {
            return "invalid";
        }
        questCount++;
        quests[questCount - 1] = new Quest(
            questCount,
            name.trim(),
            description.trim(),
            Integer.parseInt(rewardStr.trim()),
            Integer.parseInt(bonusExpStr.trim()),
            difficulty.trim().toLowerCase(),
            Integer.parseInt(daysRequiredStr.trim())
        );
        return "success";
    }

    // ===================== Wanderer Management =====================

    /** @return Nomor pengembara berikutnya (untuk ditampilkan saat penambahan) */
    public int getNextWandererNumber() { return wandererCount + 1; }

    /**
     * Menambahkan pengembara baru dengan validasi input.
     * @param name     Nama pengembara
     * @param username Username pengembara
     * @param password Password pengembara
     * @return "invalid" jika format input salah, "username_taken" jika username sudah dipakai,
     *         "success" jika berhasil
     */
    public String addWanderer(String name, String username, String password) {
        boolean nameOk = isValidWandererName(name);
        boolean usernameFormatOk = isValidUsernameFormat(username);
        boolean passwordOk = (password != null && !password.isEmpty());

        if (!nameOk || !usernameFormatOk || !passwordOk) {
            return "invalid";
        }
        if (!isUsernameAvailable(username)) {
            return "username_taken";
        }
        wandererCount++;
        wanderers[wandererCount - 1] = new Wanderer(wandererCount, name.trim(), username.trim(), password);
        return "success";
    }

    // ===================== Display Data =====================

    /**
     * Mengembalikan string daftar semua quest.
     * @return String daftar quest
     */
    public String getQuestsDisplay() {
        if (questCount == 0) {
            return "Quest yang terdaftar:\n(Belum ada quest yang terdaftar.)";
        }
        StringBuilder sb = new StringBuilder("Quest yang terdaftar:");
        for (int i = 0; i < questCount; i++) {
            sb.append("\n").append(quests[i].getDisplayString());
        }
        return sb.toString();
    }

    /**
     * Mengembalikan string daftar semua pengembara.
     * @return String daftar pengembara
     */
    public String getWanderersDisplay() {
        if (wandererCount == 0) {
            return "Pengembara yang terdaftar:\n(Belum ada pengembara yang terdaftar.)";
        }
        StringBuilder sb = new StringBuilder("Pengembara yang terdaftar:");
        for (int i = 0; i < wandererCount; i++) {
            sb.append("\n").append(wanderers[i].getDisplayString());
        }
        return sb.toString();
    }

    /**
     * Mengembalikan data diri pengembara berdasarkan ID.
     * @param wandererId ID pengembara
     * @return String data diri pengembara dengan header "=== Data Diri ==="
     */
    public String getWandererDataDisplay(String wandererId) {
        Wanderer w = findWandererById(wandererId);
        if (w == null) return "Pengembara tidak ditemukan.";
        return "=== Data Diri ===\n" + w.getDisplayString();
    }

    // ===================== Take Quest =====================

    /**
     * Pengembara mengambil quest.
     * @param wandererId ID pengembara
     * @param questId    ID quest yang ingin diambil
     * @return "not_available" (pengembara sedang dalam quest),
     *         "quest_not_found" (quest tidak ada/sudah diambil/selesai),
     *         "level_insufficient" (level tidak memenuhi syarat),
     *         "success" jika berhasil
     */
    public String takeQuest(String wandererId, String questId) {
        Wanderer wanderer = findWandererById(wandererId);
        if (wanderer == null) return "wanderer_not_found";
        if (!wanderer.isAvailable()) return "not_available";

        Quest quest = null;
        for (int i = 0; i < questCount; i++) {
            if (quests[i].getId().equalsIgnoreCase(questId) && quests[i].isAvailable()) {
                quest = quests[i];
                break;
            }
        }
        if (quest == null) return "quest_not_found";
        if (!wanderer.canTakeQuest(quest.getDifficulty())) return "level_insufficient";

        quest.assignTo(wandererId, currentDay);
        wanderer.startQuest();
        return "success";
    }

    /**
     * Mengecek apakah pengembara sedang dalam quest.
     * @param wandererId ID pengembara
     * @return true jika sedang dalam quest
     */
    public boolean isWandererInQuest(String wandererId) {
        Wanderer w = findWandererById(wandererId);
        if (w == null) return false;
        return !w.isAvailable();
    }

    // ===================== Filter Quest =====================

    /**
     * Memfilter quest berdasarkan status.
     * @param status Status yang difilter ("tersedia", "diambil", "selesai")
     * @return String daftar quest terfilter
     */
    public String filterQuestsByStatus(String status) {
        StringBuilder sb = new StringBuilder("Daftar quest terfilter:");
        boolean found = false;
        for (int i = 0; i < questCount; i++) {
            if (quests[i].getStatus().equals(status)) {
                sb.append("\n").append(quests[i].getDisplayString());
                found = true;
            }
        }
        if (!found) sb.append("\n(Tidak ada quest dengan status tersebut.)");
        return sb.toString();
    }

    /**
     * Memfilter quest berdasarkan tingkat kesulitan.
     * @param difficulty Tingkat kesulitan yang difilter
     * @return String daftar quest terfilter
     */
    public String filterQuestsByDifficulty(String difficulty) {
        StringBuilder sb = new StringBuilder("Daftar quest terfilter:");
        boolean found = false;
        for (int i = 0; i < questCount; i++) {
            if (quests[i].getDifficulty().equalsIgnoreCase(difficulty)) {
                sb.append("\n").append(quests[i].getDisplayString());
                found = true;
            }
        }
        if (!found) sb.append("\n(Tidak ada quest dengan tingkat kesulitan tersebut.)");
        return sb.toString();
    }

    // ===================== Filter Wanderer =====================

    /**
     * Memfilter pengembara berdasarkan status.
     * @param status Status yang difilter ("kosong" atau "dalam quest")
     * @return String daftar pengembara terfilter
     */
    public String filterWanderersByStatus(String status) {
        StringBuilder sb = new StringBuilder("Daftar pengembara terfilter:");
        boolean found = false;
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].getStatus().equals(status)) {
                sb.append("\n").append(wanderers[i].getDisplayString());
                found = true;
            }
        }
        if (!found) sb.append("\n(Tidak ada pengembara dengan status tersebut.)");
        return sb.toString();
    }

    /**
     * Memfilter pengembara berdasarkan rentang level (inklusif).
     * @param min Batas bawah level
     * @param max Batas atas level
     * @return String daftar pengembara terfilter
     */
    public String filterWanderersByLevelRange(int min, int max) {
        StringBuilder sb = new StringBuilder("Daftar pengembara terfilter:");
        boolean found = false;
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].getLevel() >= min && wanderers[i].getLevel() <= max) {
                sb.append("\n").append(wanderers[i].getDisplayString());
                found = true;
            }
        }
        if (!found) sb.append("\n(Tidak ada pengembara dalam rentang level tersebut.)");
        return sb.toString();
    }

    // ===================== Sort Quest =====================

    /**
     * Mengurutkan dan menampilkan daftar quest berdasarkan tingkat kesulitan.
     * @param order "asc" atau "desc"
     * @return String daftar quest terurut
     */
    public String sortQuestsByDifficulty(String order) {
        Quest[] sorted = copyQuestArray();
        // Bubble sort berdasarkan bobot kesulitan
        for (int i = 0; i < questCount - 1; i++) {
            for (int j = 0; j < questCount - 1 - i; j++) {
                int w1 = difficultyWeight(sorted[j].getDifficulty());
                int w2 = difficultyWeight(sorted[j + 1].getDifficulty());
                boolean shouldSwap = order.equalsIgnoreCase("asc") ? w1 > w2 : w1 < w2;
                if (shouldSwap) {
                    Quest tmp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = tmp;
                }
            }
        }
        return buildQuestListString(sorted);
    }

    /**
     * Mengurutkan dan menampilkan daftar quest berdasarkan reward.
     * @param order "asc" atau "desc"
     * @return String daftar quest terurut
     */
    public String sortQuestsByReward(String order) {
        Quest[] sorted = copyQuestArray();
        for (int i = 0; i < questCount - 1; i++) {
            for (int j = 0; j < questCount - 1 - i; j++) {
                boolean shouldSwap = order.equalsIgnoreCase("asc")
                    ? sorted[j].getReward() > sorted[j + 1].getReward()
                    : sorted[j].getReward() < sorted[j + 1].getReward();
                if (shouldSwap) {
                    Quest tmp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = tmp;
                }
            }
        }
        return buildQuestListString(sorted);
    }

    // ===================== Sort Wanderer =====================

    /**
     * Mengurutkan dan menampilkan daftar pengembara berdasarkan nama (lexicographic, case-insensitive).
     * @param order "asc" atau "desc"
     * @return String daftar pengembara terurut
     */
    public String sortWanderersByName(String order) {
        Wanderer[] sorted = copyWandererArray();
        for (int i = 0; i < wandererCount - 1; i++) {
            for (int j = 0; j < wandererCount - 1 - i; j++) {
                int cmp = sorted[j].getName().compareToIgnoreCase(sorted[j + 1].getName());
                boolean shouldSwap = order.equalsIgnoreCase("asc") ? cmp > 0 : cmp < 0;
                if (shouldSwap) {
                    Wanderer tmp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = tmp;
                }
            }
        }
        return buildWandererListString(sorted);
    }

    /**
     * Mengurutkan dan menampilkan daftar pengembara berdasarkan level.
     * @param order "asc" atau "desc"
     * @return String daftar pengembara terurut
     */
    public String sortWanderersByLevel(String order) {
        Wanderer[] sorted = copyWandererArray();
        for (int i = 0; i < wandererCount - 1; i++) {
            for (int j = 0; j < wandererCount - 1 - i; j++) {
                boolean shouldSwap = order.equalsIgnoreCase("asc")
                    ? sorted[j].getLevel() > sorted[j + 1].getLevel()
                    : sorted[j].getLevel() < sorted[j + 1].getLevel();
                if (shouldSwap) {
                    Wanderer tmp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = tmp;
                }
            }
        }
        return buildWandererListString(sorted);
    }

    // ===================== Validation Helpers =====================

    /**
     * Mengecek apakah username tersedia (tidak dipakai admin atau pengembara lain).
     * @param username Username yang dicek
     * @return true jika username tersedia
     */
    public boolean isUsernameAvailable(String username) {
        if (ADMIN_USERNAME.equalsIgnoreCase(username)) return false;
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].getUsername().equals(username)) return false;
        }
        return true;
    }

    /**
     * Memvalidasi teks quest (nama/deskripsi): hanya alfanumerik dan spasi, tidak boleh kosong.
     * @param text Teks yang dicek
     * @return true jika valid
     */
    public boolean isValidQuestTextField(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        return text.matches("[a-zA-Z0-9 ]+");
    }

    /**
     * Memvalidasi string sebagai bilangan bulat non-negatif.
     * @param s String yang dicek
     * @return true jika valid
     */
    public boolean isValidNonNegativeInt(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(s.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Memvalidasi string sebagai bilangan bulat positif.
     * @param s String yang dicek
     * @return true jika valid
     */
    public boolean isValidPositiveInt(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(s.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Memvalidasi tingkat kesulitan quest.
     * @param difficulty Tingkat kesulitan yang dicek
     * @return true jika valid (mudah/menengah/sulit, case-insensitive)
     */
    public boolean isValidDifficulty(String difficulty) {
        if (difficulty == null) return false;
        String d = difficulty.trim().toLowerCase();
        return d.equals("mudah") || d.equals("menengah") || d.equals("sulit");
    }

    /**
     * Memvalidasi nama pengembara: hanya huruf dan spasi, tidak kosong, title case (kapital di awal tiap kata).
     * @param name Nama yang dicek
     * @return true jika valid
     */
    public boolean isValidWandererName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        if (!name.matches("[a-zA-Z ]+")) return false;
        // Tidak boleh ada spasi ganda
        if (name.contains("  ")) return false;
        // Trim dan cek title case
        String trimmed = name.trim();
        String[] parts = trimmed.split(" ");
        for (String part : parts) {
            if (part.isEmpty()) return false;
            if (!Character.isUpperCase(part.charAt(0))) return false;
            for (int i = 1; i < part.length(); i++) {
                if (!Character.isLowerCase(part.charAt(i))) return false;
            }
        }
        return true;
    }

    /**
     * Memvalidasi format username: satu atau lebih karakter huruf, angka, atau underscore.
     * @param username Username yang dicek
     * @return true jika valid
     */
    public boolean isValidUsernameFormat(String username) {
        if (username == null || username.isEmpty()) return false;
        return username.matches("[a-zA-Z0-9_]+");
    }

    /**
     * Memvalidasi status quest untuk filter.
     * @param status Status yang dicek
     * @return true jika valid ("tersedia", "diambil", "selesai")
     */
    public boolean isValidQuestStatus(String status) {
        return status.equals("tersedia") || status.equals("diambil") || status.equals("selesai");
    }

    /**
     * Memvalidasi status pengembara untuk filter.
     * @param status Status yang dicek
     * @return true jika valid ("kosong", "dalam quest")
     */
    public boolean isValidWandererStatus(String status) {
        return status.equals("kosong") || status.equals("dalam quest");
    }

    // ===================== Private Helpers =====================

    /**
     * Mencari pengembara berdasarkan ID.
     * @param id ID pengembara
     * @return Objek Wanderer, atau null jika tidak ditemukan
     */
    private Wanderer findWandererById(String id) {
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].getId().equals(id)) return wanderers[i];
        }
        return null;
    }

    /**
     * Menyalin array quest aktif ke array baru (hanya sebanyak questCount).
     * @return Salinan array quest
     */
    private Quest[] copyQuestArray() {
        Quest[] copy = new Quest[questCount];
        for (int i = 0; i < questCount; i++) copy[i] = quests[i];
        return copy;
    }

    /**
     * Menyalin array pengembara aktif ke array baru (hanya sebanyak wandererCount).
     * @return Salinan array pengembara
     */
    private Wanderer[] copyWandererArray() {
        Wanderer[] copy = new Wanderer[wandererCount];
        for (int i = 0; i < wandererCount; i++) copy[i] = wanderers[i];
        return copy;
    }

    /**
     * Membangun string daftar quest dari array.
     * @param arr Array quest
     * @return String daftar quest terurut
     */
    private String buildQuestListString(Quest[] arr) {
        StringBuilder sb = new StringBuilder("Daftar quest terurut:");
        for (Quest q : arr) {
            sb.append("\n").append(q.getDisplayString());
        }
        return sb.toString();
    }

    /**
     * Membangun string daftar pengembara dari array.
     * @param arr Array pengembara
     * @return String daftar pengembara terurut
     */
    private String buildWandererListString(Wanderer[] arr) {
        StringBuilder sb = new StringBuilder("Daftar pengembara terurut:");
        for (Wanderer w : arr) {
            sb.append("\n").append(w.getDisplayString());
        }
        return sb.toString();
    }

    /**
     * Mengembalikan bobot numerik tingkat kesulitan untuk pengurutan.
     * mudah=1, menengah=2, sulit=3
     * @param difficulty Tingkat kesulitan
     * @return Bobot numerik
     */
    private int difficultyWeight(String difficulty) {
        if (difficulty.equals("mudah")) return 1;
        if (difficulty.equals("menengah")) return 2;
        if (difficulty.equals("sulit")) return 3;
        return 0;
    }
}