import java.util.Scanner;

/**
 * Class utama program BurhanQuest v2.
 * Mengatur seluruh Input/Output. Tidak mengandung logika bisnis.
 *
 * Nama  : Marclay Ardell Taufiqurrachman Harahap
 * NPM   : 2506621024
 */
public class Main2506621024 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GameManager gm = new GameManager();

        // Cetak Banner ASCII Art
        System.out.println("                               ,,                                       ");
        System.out.println("`7MM\"\"\"Yp,                   `7MM                                       ");
        System.out.println("  MM    Yb                     MM                                       ");
        System.out.println("  MM    dP `7MM  `7MM `7Mb,od8 MMpMMMb.  ,6\"Yb. `7MMpMMMb.              ");
        System.out.println("  MM\"\"\"bg.   MM    MM   MM' \"' MM    MM 8)   MM   MM    MM              ");
        System.out.println("  MM    `Y   MM    MM   MM     MM    MM  ,pm9MM   MM    MM              ");
        System.out.println("  MM    ,9   MM    MM   MM     MM    MM 8M   MM   MM    MM              ");
        System.out.println(".JqMmmpd9    `Mbod\"YML.JMML. .JMML  JMML`Moo9^Yo.JMML  JMML.            ");
        System.out.println("   \\ /      .g8\"\"8q.                                 mm      `7MMF`7MMF'");
        System.out.println("o=--*--=o .dP'    `YM.                               MM        MM   MM  ");
        System.out.println("   / \\    dM'      `MM `7MM  `7MM  .gP\"Ya  ,pP\"Ybd mmMMmm      MM   MM  ");
        System.out.println("  d   b   MM        MM   MM    MM ,M'   Yb 8I   `\"   MM        MM   MM  ");
        System.out.println("          MM.      ,MP   MM    MM 8M\"\"\"\"\"\" `YMMMa.   MM        MM   MM  ");
        System.out.println("          `Mb.    ,dP'   MM    MM YM.    , L.   I8   MM        MM   MM  ");
        System.out.println("            `\"bmmd\"'     `Mbod\"YML.`Mbmmd' M9mmmP'   `Mbmo   .JMML.JMML.");
        System.out.println("                MMb                                                     ");
        System.out.println("                 `bood'                                                 ");

        System.out.println("Selamat datang di BurhanQuest!");

        // Loop utama program
        boolean running = true;
        while (running) {
            System.out.println("\n=== Hari ke-" + gm.getCurrentDay() + " ===");
            System.out.println("1. Login");
            System.out.println("2. Keluar dari program");
            System.out.print("Masukkan pilihan: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin(sc, gm);
                    break;
                case "2":
                    System.out.println("Terima kasih telah menggunakan BurhanQuest!");
                    System.out.println("Dibuat oleh: Marclay Ardell Taufiqurrachman Harahap - 2506621024");
                    running = false;
                    break;
                default:
                    // Pilihan tidak dikenal, ulang loop
                    break;
            }
        }
        sc.close();
    }

    // ===================== Login Handler =====================

    /**
     * Menangani proses login pengguna.
     */
    private static void handleLogin(Scanner sc, GameManager gm) {
        System.out.print("Masukkan username: ");
        String username = sc.nextLine();
        System.out.print("Masukkan password: ");
        String password = sc.nextLine();

        String result = gm.login(username, password);
        if (result == null) {
            System.out.println("Username atau password salah.");
        } else if (result.equals("admin")) {
            System.out.println("Login berhasil! Selamat datang, Admin.");
            handleAdminMenu(sc, gm);
        } else {
            System.out.println("Login berhasil! Selamat datang, " + result + ".");
            String wandererId = gm.getWandererIdByUsername(username);
            handleWandererMenu(sc, gm, result, wandererId);
        }
    }

    // ===================== Admin Menu =====================

    /**
     * Menampilkan dan menangani Menu Admin.
     */
    private static void handleAdminMenu(Scanner sc, GameManager gm) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== Menu Admin (Hari ke-" + gm.getCurrentDay() + ") ===");
            System.out.println("1. Lihat daftar quest");
            System.out.println("2. Lihat daftar pengembara");
            System.out.println("3. Tambah Quest");
            System.out.println("4. Tambah Pengembara");
            System.out.println("5. Filter daftar quest");
            System.out.println("6. Filter daftar pengembara");
            System.out.println("7. Tampilkan daftar quest terurut");
            System.out.println("8. Tampilkan daftar pengembara terurut");
            System.out.println("9. Lanjut ke hari berikutnya");
            System.out.println("0. Keluar");
            System.out.print("Masukkan pilihan: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println(gm.getQuestsDisplay());
                    break;
                case "2":
                    System.out.println(gm.getWanderersDisplay());
                    break;
                case "3":
                    handleAddQuest(sc, gm);
                    break;
                case "4":
                    handleAddWanderer(sc, gm);
                    break;
                case "5":
                    handleFilterQuest(sc, gm);
                    break;
                case "6":
                    handleFilterWanderer(sc, gm);
                    break;
                case "7":
                    handleSortQuest(sc, gm);
                    break;
                case "8":
                    handleSortWanderer(sc, gm);
                    break;
                case "9":
                    System.out.println(gm.advanceDay());
                    break;
                case "0":
                    System.out.println("Logout berhasil.");
                    loggedIn = false;
                    break;
                default:
                    break;
            }
        }
    }

    // ===================== Tambah Quest =====================

    /**
     * Menangani alur tambah quest dengan re-prompt bila input tidak valid.
     */
    private static void handleAddQuest(Scanner sc, GameManager gm) {
        boolean added = false;
        while (!added) {
            System.out.println("Quest " + gm.getNextQuestNumber());
            System.out.print("Masukkan nama quest: ");
            String name = sc.nextLine();
            System.out.print("Masukkan deskripsi quest: ");
            String description = sc.nextLine();
            System.out.print("Masukkan reward quest berupa bilangan bulat nonnegatif: ");
            String reward = sc.nextLine();
            System.out.print("Masukkan bonus exp quest berupa bilangan bulat nonnegatif: ");
            String bonusExp = sc.nextLine();
            System.out.print("Masukkan tingkat kesulitan quest (opsi: mudah, menengah, sulit): ");
            String difficulty = sc.nextLine();
            System.out.print("Masukkan durasi quest (dalam hari, bilangan bulat positif): ");
            String daysRequired = sc.nextLine();

            String result = gm.addQuest(name, description, reward, bonusExp, difficulty, daysRequired);
            if (result.equals("success")) {
                System.out.println("Quest berhasil ditambahkan.");
                added = true;
            } else {
                System.out.println("Input tidak valid. Harap masukkan data dengan benar.");
            }
        }
    }

    // ===================== Tambah Pengembara =====================

    /**
     * Menangani alur tambah pengembara dengan re-prompt bila input tidak valid.
     */
    private static void handleAddWanderer(Scanner sc, GameManager gm) {
        boolean added = false;
        while (!added) {
            System.out.println("Pengembara " + gm.getNextWandererNumber());
            System.out.print("Masukkan nama pengembara: ");
            String name = sc.nextLine();
            System.out.print("Masukkan username pengembara: ");
            String username = sc.nextLine();
            System.out.print("Masukkan password pengembara: ");
            String password = sc.nextLine();

            String result = gm.addWanderer(name, username, password);
            if (result.equals("success")) {
                System.out.println("Pengembara berhasil ditambahkan.");
                added = true;
            } else if (result.equals("username_taken")) {
                System.out.println("Username sudah digunakan. Harap pilih username lain.");
                // Tidak set added = true, loop ulang
            } else {
                // invalid
                System.out.println("Input tidak valid. Harap masukkan data dengan benar.");
            }
        }
    }

    // ===================== Filter Quest =====================

    /**
     * Menangani submenu filter daftar quest.
     */
    private static void handleFilterQuest(Scanner sc, GameManager gm) {
        boolean inFilterMenu = true;
        while (inFilterMenu) {
            System.out.println("Filter daftar quest");
            System.out.println("1. Filter berdasarkan status");
            System.out.println("2. Filter berdasarkan tingkat kesulitan");
            System.out.println("X. Kembali ke menu utama");
            System.out.print("Masukkan tipe filter: ");
            String tipe = sc.nextLine().trim();

            if (tipe.equalsIgnoreCase("X")) {
                inFilterMenu = false;
            } else if (tipe.equals("1")) {
                System.out.print("Masukkan status quest yang ingin difilter (tersedia/diambil/selesai),"
                    + " masukan 'x' atau 'X' untuk kembali ke menu utama: ");
                String status = sc.nextLine().trim();
                if (status.equalsIgnoreCase("x")) {
                    inFilterMenu = false;
                } else if (gm.isValidQuestStatus(status)) {
                    System.out.println(gm.filterQuestsByStatus(status));
                    inFilterMenu = false;
                } else {
                    System.out.println("Pilihan tidak valid. Harap masukkan pilihan dengan benar.");
                }
            } else if (tipe.equals("2")) {
                System.out.print("Masukkan tingkat kesulitan quest yang ingin difilter"
                    + " (mudah/menengah/sulit), masukan 'x' atau 'X' untuk kembali ke menu utama: ");
                String difficulty = sc.nextLine().trim();
                if (difficulty.equalsIgnoreCase("x")) {
                    inFilterMenu = false;
                } else if (gm.isValidDifficulty(difficulty)) {
                    System.out.println(gm.filterQuestsByDifficulty(difficulty.toLowerCase()));
                    inFilterMenu = false;
                } else {
                    System.out.println("Pilihan tidak valid. Harap masukkan pilihan dengan benar.");
                }
            } else {
                System.out.println("Pilihan tidak valid. Harap masukkan pilihan dengan benar.");
            }
        }
    }

    // ===================== Filter Wanderer =====================

    /**
     * Menangani submenu filter daftar pengembara.
     */
    private static void handleFilterWanderer(Scanner sc, GameManager gm) {
        boolean inFilterMenu = true;
        while (inFilterMenu) {
            System.out.println("Filter daftar pengembara");
            System.out.println("1. Filter berdasarkan status");
            System.out.println("2. Filter berdasarkan rentang level");
            System.out.println("X. Kembali ke menu utama");
            System.out.print("Masukkan tipe filter: ");
            String tipe = sc.nextLine().trim();

            if (tipe.equalsIgnoreCase("X")) {
                inFilterMenu = false;
            } else if (tipe.equals("1")) {
                System.out.print("Masukkan status pengembara yang ingin difilter (kosong/dalam quest),"
                    + " masukan 'x' atau 'X' untuk kembali ke menu utama: ");
                String status = sc.nextLine().trim();
                if (status.equalsIgnoreCase("x")) {
                    inFilterMenu = false;
                } else if (gm.isValidWandererStatus(status)) {
                    System.out.println(gm.filterWanderersByStatus(status));
                    inFilterMenu = false;
                } else {
                    System.out.println("Pilihan tidak valid. Harap masukkan pilihan dengan benar.");
                }
            } else if (tipe.equals("2")) {
                System.out.print("Masukkan rentang level (inklusif) yang ingin difilter,"
                    + " masukan 'x' atau 'X' untuk kembali ke menu utama:\n");
                System.out.print("Masukkan batas bawah: ");
                String minStr = sc.nextLine().trim();
                if (minStr.equalsIgnoreCase("x")) {
                    inFilterMenu = false;
                    continue;
                }
                System.out.print("Masukkan batas atas: ");
                String maxStr = sc.nextLine().trim();
                if (maxStr.equalsIgnoreCase("x")) {
                    inFilterMenu = false;
                    continue;
                }
                if (gm.isValidPositiveInt(minStr) && gm.isValidPositiveInt(maxStr)) {
                    int min = Integer.parseInt(minStr);
                    int max = Integer.parseInt(maxStr);
                    System.out.println(gm.filterWanderersByLevelRange(min, max));
                    inFilterMenu = false;
                } else {
                    System.out.println("Pilihan tidak valid. Harap masukkan pilihan dengan benar.");
                }
            } else {
                System.out.println("Pilihan tidak valid. Harap masukkan pilihan dengan benar.");
            }
        }
    }

    // ===================== Sort Quest =====================

    /**
     * Menangani submenu pengurutan daftar quest.
     */
    private static void handleSortQuest(Scanner sc, GameManager gm) {
        boolean inSortMenu = true;
        while (inSortMenu) {
            System.out.println("Urutkan daftar quest");
            System.out.println("1. Berdasarkan tingkat kesulitan");
            System.out.println("2. Berdasarkan reward");
            System.out.println("X. Kembali ke menu utama");
            System.out.print("Masukkan input: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("X")) {
                inSortMenu = false;
            } else if (input.equals("1") || input.equals("2")) {
                System.out.print("Masukkan order urutan (asc/desc),"
                    + " masukkan x untuk kembali ke menu utama: ");
                String order = sc.nextLine().trim();
                if (order.equalsIgnoreCase("x")) {
                    inSortMenu = false;
                } else if (order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc")) {
                    if (input.equals("1")) {
                        System.out.println(gm.sortQuestsByDifficulty(order));
                    } else {
                        System.out.println(gm.sortQuestsByReward(order));
                    }
                    inSortMenu = false;
                } else {
                    System.out.println("Urutan tidak valid. Harap masukkan urutan dengan benar.");
                }
            } else {
                System.out.println("Pilihan tidak valid. Harap masukkan pilihan dengan benar.");
            }
        }
    }

    // ===================== Sort Wanderer =====================

    /**
     * Menangani submenu pengurutan daftar pengembara.
     */
    private static void handleSortWanderer(Scanner sc, GameManager gm) {
        boolean inSortMenu = true;
        while (inSortMenu) {
            System.out.println("Urutkan daftar pengembara");
            System.out.println("1. Berdasarkan nama");
            System.out.println("2. Berdasarkan level");
            System.out.println("X. Kembali ke menu utama");
            System.out.print("Masukkan input: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("X")) {
                inSortMenu = false;
            } else if (input.equals("1") || input.equals("2")) {
                System.out.print("Masukkan order urutan (asc/desc),"
                    + " masukkan x untuk kembali ke menu utama: ");
                String order = sc.nextLine().trim();
                if (order.equalsIgnoreCase("x")) {
                    inSortMenu = false;
                } else if (order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc")) {
                    if (input.equals("1")) {
                        System.out.println(gm.sortWanderersByName(order));
                    } else {
                        System.out.println(gm.sortWanderersByLevel(order));
                    }
                    inSortMenu = false;
                } else {
                    System.out.println("Urutan tidak valid. Harap masukkan urutan dengan benar.");
                }
            } else {
                System.out.println("Pilihan tidak valid. Harap masukkan pilihan dengan benar.");
            }
        }
    }

    // ===================== Wanderer Menu =====================

    /**
     * Menampilkan dan menangani Menu Pengembara.
     * @param gm         Instance GameManager
     * @param name       Nama pengembara yang login
     * @param wandererId ID pengembara yang login
     */
    private static void handleWandererMenu(Scanner sc, GameManager gm,
                                           String name, String wandererId) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n=== Menu Pengembara: " + name + " (Hari ke-" + gm.getCurrentDay() + ") ===");
            System.out.println("1. Lihat data diri");
            System.out.println("2. Lihat daftar quest");
            System.out.println("3. Filter daftar quest");
            System.out.println("4. Tampilkan daftar quest terurut");
            System.out.println("5. Ambil quest");
            System.out.println("0. Keluar");
            System.out.print("Masukkan pilihan: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println(gm.getWandererDataDisplay(wandererId));
                    break;
                case "2":
                    System.out.println(gm.getQuestsDisplay());
                    break;
                case "3":
                    handleFilterQuest(sc, gm);
                    break;
                case "4":
                    handleSortQuest(sc, gm);
                    break;
                case "5":
                    handleTakeQuest(sc, gm, wandererId);
                    break;
                case "0":
                    System.out.println("Logout berhasil.");
                    loggedIn = false;
                    break;
                default:
                    break;
            }
        }
    }

    // ===================== Take Quest Handler =====================

    /**
     * Menangani alur pengambilan quest oleh pengembara.
     */
    private static void handleTakeQuest(Scanner sc, GameManager gm, String wandererId) {
        // Cek apakah sedang dalam quest
        if (gm.isWandererInQuest(wandererId)) {
            System.out.println("Kamu sedang dalam quest. Selesaikan quest terlebih dahulu.");
            return;
        }

        // Loop sampai quest berhasil diambil atau pengguna membatalkan
        boolean taking = true;
        while (taking) {
            System.out.print("Masukkan ID Quest yang ingin diambil (atau 'X'/'x' untuk kembali): ");
            String questId = sc.nextLine().trim();

            if (questId.equalsIgnoreCase("X")) {
                taking = false;
            } else {
                String result = gm.takeQuest(wandererId, questId);
                switch (result) {
                    case "success":
                        System.out.println("Quest berhasil diambil!");
                        taking = false;
                        break;
                    case "quest_not_found":
                        System.out.println("Quest tidak ditemukan atau sudah diambil/selesai.");
                        break;
                    case "level_insufficient":
                        System.out.println("Level kamu belum memenuhi persyaratan untuk quest ini.");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}