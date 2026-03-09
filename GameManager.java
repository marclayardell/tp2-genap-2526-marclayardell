// class yang ngatur semua logika program, mulai dari login, tambah quest/pengembara, ambil quest, sampai filter/sort
public class GameManager {

    // kredensial admin, hardcode sesuai soal
    private static final String ADMIN_USERNAME = "burhan";
    private static final String ADMIN_PASSWORD = "burunghantu123";

    // pake array biasa, ga boleh pake arraylist/collections
    private Quest[] quests = new Quest[1000];
    private int questCount = 0;

    private Wanderer[] wanderers = new Wanderer[1000];
    private int wandererCount = 0;

    private int currentDay = 1; // hari dimulai dari 1

    // --- sistem hari ---

    public int getCurrentDay() { return currentDay; }

    // majuin hari, sekalian cek quest mana yang udah selesai
    public String advanceDay() {
        currentDay++;
        StringBuilder sb = new StringBuilder("Hari berganti menjadi hari ke-" + currentDay + ".");

        boolean anyCompleted = false;
        for (int i = 0; i < questCount; i++) {
            // quest selesai kalau hari ini >= hari ambil + durasi
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

    // --- login ---

    // return "admin" kalau admin, return nama kalau pengembara, null kalau salah
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

    // cari id pengembara dari username nya
    public String getWandererIdByUsername(String username) {
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].getUsername().equals(username)) {
                return wanderers[i].getId();
            }
        }
        return null;
    }

    // --- tambah quest ---

    // buat nampilin nomor quest berikutnya waktu input
    public int getNextQuestNumber() { return questCount + 1; }

    // validasi semua input dulu, kalau ada yang salah return "invalid"
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

    // --- tambah pengembara ---

    public int getNextWandererNumber() { return wandererCount + 1; }

    // validasi nama, username, password. return "invalid", "username_taken", atau "success"
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

    // --- tampilkan data ---

    // nampilin semua quest
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

    // nampilin semua pengembara
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

    // nampilin data diri pengembara yang lagi login
    public String getWandererDataDisplay(String wandererId) {
        Wanderer w = findWandererById(wandererId);
        if (w == null) return "Pengembara tidak ditemukan.";
        return "=== Data Diri ===\n" + w.getDisplayString();
    }

    // --- ambil quest ---

    // pengembara minta ngambil quest, ada beberapa kondisi yang dicek
    public String takeQuest(String wandererId, String questId) {
        Wanderer wanderer = findWandererById(wandererId);
        if (wanderer == null) return "wanderer_not_found";
        if (!wanderer.isAvailable()) return "not_available"; // lagi dalam quest

        // cari quest yang id nya cocok dan masih tersedia
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

    // ngecek apakah pengembara lagi dalam quest
    public boolean isWandererInQuest(String wandererId) {
        Wanderer w = findWandererById(wandererId);
        if (w == null) return false;
        return !w.isAvailable();
    }

    // --- filter quest ---

    // filter quest berdasarkan status (tersedia/diambil/selesai)
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

    // filter quest berdasarkan tingkat kesulitan
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

    // --- filter pengembara ---

    // filter pengembara berdasarkan status (kosong / dalam quest)
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

    // filter pengembara berdasarkan rentang level, inklusif
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

    // --- sort quest ---

    // urutkan quest berdasarkan tingkat kesulitan (mudah < menengah < sulit)
    public String sortQuestsByDifficulty(String order) {
        Quest[] sorted = copyQuestArray();
        // bubble sort
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

    // urutkan quest berdasarkan reward
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

    // --- sort pengembara ---

    // urutkan pengembara berdasarkan nama, case-insensitive
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

    // urutkan pengembara berdasarkan level
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

    // --- validasi input ---

    // cek apakah username belum dipakai (admin atau pengembara lain)
    public boolean isUsernameAvailable(String username) {
        if (ADMIN_USERNAME.equalsIgnoreCase(username)) return false;
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].getUsername().equals(username)) return false;
        }
        return true;
    }

    // nama/deskripsi quest hanya boleh huruf, angka, dan spasi, ga boleh kosong
    public boolean isValidQuestTextField(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        return text.matches("[a-zA-Z0-9 ]+");
    }

    // reward/bonusexp harus angka >= 0
    public boolean isValidNonNegativeInt(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(s.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // durasi hari harus angka > 0
    public boolean isValidPositiveInt(String s) {
        if (s == null || s.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(s.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // tingkat kesulitan harus salah satu dari tiga ini
    public boolean isValidDifficulty(String difficulty) {
        if (difficulty == null) return false;
        String d = difficulty.trim().toLowerCase();
        return d.equals("mudah") || d.equals("menengah") || d.equals("sulit");
    }

    // nama pengembara hanya boleh huruf dan spasi, title case, ga ada spasi ganda
    public boolean isValidWandererName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        if (!name.matches("[a-zA-Z ]+")) return false;
        if (name.contains("  ")) return false; // ga boleh spasi ganda
        String trimmed = name.trim();
        String[] parts = trimmed.split(" ");
        for (String part : parts) {
            if (part.isEmpty()) return false;
            if (!Character.isUpperCase(part.charAt(0))) return false; // huruf pertama harus kapital
            for (int i = 1; i < part.length(); i++) {
                if (!Character.isLowerCase(part.charAt(i))) return false; // sisanya harus lowercase
            }
        }
        return true;
    }

    // username hanya boleh huruf, angka, underscore
    public boolean isValidUsernameFormat(String username) {
        if (username == null || username.isEmpty()) return false;
        return username.matches("[a-zA-Z0-9_]+");
    }

    // status quest yang valid buat filter
    public boolean isValidQuestStatus(String status) {
        return status.equals("tersedia") || status.equals("diambil") || status.equals("selesai");
    }

    // status pengembara yang valid buat filter
    public boolean isValidWandererStatus(String status) {
        return status.equals("kosong") || status.equals("dalam quest");
    }

    // --- helper private ---

    // nyari pengembara berdasarkan id
    private Wanderer findWandererById(String id) {
        for (int i = 0; i < wandererCount; i++) {
            if (wanderers[i].getId().equals(id)) return wanderers[i];
        }
        return null;
    }

    // copy array quest buat sorting (biar yang asli ga keubah)
    private Quest[] copyQuestArray() {
        Quest[] copy = new Quest[questCount];
        for (int i = 0; i < questCount; i++) copy[i] = quests[i];
        return copy;
    }

    // copy array pengembara buat sorting
    private Wanderer[] copyWandererArray() {
        Wanderer[] copy = new Wanderer[wandererCount];
        for (int i = 0; i < wandererCount; i++) copy[i] = wanderers[i];
        return copy;
    }

    // build string dari array quest, dipake setelah sorting
    private String buildQuestListString(Quest[] arr) {
        StringBuilder sb = new StringBuilder("Daftar quest terurut:");
        for (Quest q : arr) {
            sb.append("\n").append(q.getDisplayString());
        }
        return sb.toString();
    }

    // build string dari array pengembara, dipake setelah sorting
    private String buildWandererListString(Wanderer[] arr) {
        StringBuilder sb = new StringBuilder("Daftar pengembara terurut:");
        for (Wanderer w : arr) {
            sb.append("\n").append(w.getDisplayString());
        }
        return sb.toString();
    }

    // konversi kesulitan ke angka buat ngebandingin waktu sorting
    // mudah=1, menengah=2, sulit=3
    private int difficultyWeight(String difficulty) {
        if (difficulty.equals("mudah")) return 1;
        if (difficulty.equals("menengah")) return 2;
        if (difficulty.equals("sulit")) return 3;
        return 0;
    }
}