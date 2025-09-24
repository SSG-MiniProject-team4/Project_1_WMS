package controller;


import domain.*;
import service.FinanceServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class FinanceControllerImpl implements FinanceController {
    private User user;
    private WarehouseAdmin whAdmin;
    private TotalAdmin totalAdmin;
    private int authority = 0;

    private boolean loop = true;

    //Service 객체
    private FinanceServiceImpl finance;
    //사용자 입력
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    // 싱글톤 패턴 적용
    private static FinanceControllerImpl instance;
    private FinanceControllerImpl() {
        this.finance = FinanceServiceImpl.getInstance();
    }
    public static FinanceControllerImpl getInstance() {
        if (instance == null) instance = new FinanceControllerImpl();
        return instance;
    }

    //메인 화면 출력 메서드, 권한에 따라 다른 메서드로 화면 출력
    @Override
    public void showFinanceMenu() {
        while(loop) {
            switch (authority) {
                case 1:
                    showTotalAdminMenu();
                    selectTotalAdminMenu();
                    break;
                case 2:
                    showWhAdminMenu();
                    selectWhAdminMenu();
                    break;
                case 3:
                    showUserMenu();
                    selectUserMenu();
                    break;
                default:
                    System.out.println("접속 불가! 권한이 존재하지 않습니다.");
            }
        }
    }

    // 권한별 메인화면
    private void showTotalAdminMenu(){
        // 관리자 화면
        System.out.print("""
                            ============================================================
                                                      재무관리
                            ============================================================
                             1.전체 재무 조회 | 2.창고별 재무 조회 | 3.메인 메뉴 | 4.로그아웃
                            >  """);
    }
    private void showWhAdminMenu(){
        //창고 관리자 화면
        System.out.print("""
                            ============================================================
                                                      재무관리
                            ============================================================
                              1.재무 조회 | 2.지출 관리 | 2.구독승인 관리 | 3.메인 메뉴 |
                              4.로그아웃
                            ============================================================
                            >  """);
    }
    private void showUserMenu(){
        //일반회원 화면
        System.out.print("""
                           ============================================================
                                                     재무관리
                           ============================================================
                                       1.구독 관리  |  2.메인 메뉴  |  3.로그아웃
                           >  """);
    }

    //권한별 메뉴선택 및 메서드 호출
    private void selectTotalAdminMenu(){
        try {
            String num = input.readLine().trim();
            switch (num) {
                case "1" -> handleGetAllFinance();
                case "2" -> handleGetWhFinance();
                case "3" -> loop = false;
                case "4" -> {
                    System.out.println("logout");
                    loop = false;
                }
                default -> System.out.println("번호를 잘못 입력했습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void selectWhAdminMenu(){
        try {
            String num = input.readLine().trim();
            switch (num) {
                case "1" -> handleGetWhFinance();
                case "2" -> System.out.println("지출 관리");
                case "3" -> System.out.println("구독 승인");
                case "4" -> loop = false;
                case "5" -> {
                    System.out.println("logout");
                    loop = false;
                }
                default -> System.out.println("번호를 잘못 입력했습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void selectUserMenu(){
        try {
            String num = input.readLine().trim();
            switch (num) {
                case "1" -> System.out.println("구독 관리");
                case "2" -> loop = false;
                case "3" -> {
                    System.out.println("logout");
                    loop = false;
                }
                default -> System.out.println("번호를 잘못 입력했습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetAllFinance() {
        String type = getFinanceType();
        String date = getFinanceDate();
        // API 메서드 호출
        Map<String, Object> result = getFinanceList(type, date);
        // 결과 출력
        printFinanceList(result, date, type);
    }

    private void handleGetWhFinance() {

    }

    @Override
    public Map<String, Object> getFinanceList(String type, String date) {
        // service 호출 후 결과 바로 반환 (입력/출력 로직 제거)
        return finance.getFinanceList(0, type, date);
    }


    private void printFinanceList(Map<String, Object> result, String date, String type) {
        boolean isYear = date.length() == 4;

        System.out.println("\n📊 [" + date + (isYear ? "년" : "월") + " " + type + " 정산 내역]");
        System.out.println("-".repeat(60));

        switch (type) {
            case "All" -> {
                if (isYear) {
                    Map<String, Map<String, Long>> monthlySummary = (Map<String, Map<String, Long>>) result.get("monthlySummary");
                    long totalSales = (long) result.get("totalSales");
                    long totalExpense = (long) result.get("totalExpense");
                    long netAmount = (long) result.get("netAmount");

                    System.out.printf("%10s | %14s | %14s | %14s \n", "월", "매출", "지출", "정산");
                    for (String month : monthlySummary.keySet()) {
                        Map<String, Long> data = monthlySummary.get(month);
                        System.out.printf("%10s | %14,d | %14,d | %14,d \n",
                                month, data.get("sales"), data.get("expense"), data.get("net"));
                    }
                    System.out.println("-".repeat(60));
                    System.out.println("총 매출: " + String.format("%,d원", totalSales));
                    System.out.println("총 지출: " + String.format("%,d원", totalExpense));
                    System.out.println("총 정산: " + String.format("%,d원", netAmount));
                } else {
                    List<Sales> salesList = (List<Sales>) result.get("salesList");
                    List<Expense> expenseList = (List<Expense>) result.get("expenseList");
                    long totalSales = (long) result.get("totalSales");
                    long totalExpense = (long) result.get("totalExpense");
                    long netAmount = (long) result.get("netAmount");

                    System.out.println("[매출 내역]");
                    System.out.printf("%5s | %15s | %21s  \n",
                            "매출번호", "매출금액", "매출발생일");
                    System.out.println("-".repeat(60));
                    for (Sales s : salesList) {
                        System.out.printf("%5d | %15d | %21s \n",
                                s.getSIdx(), s.getSPrice(), s.getSDate());
                        //System.out.printf("- [%s] %,d원\n", s.getSDate(), s.getSPrice());
                    }

                    System.out.println("-".repeat(60));
                    System.out.println("\n[지출 내역]");
                    System.out.printf("%5s | %10s | %15s | %21s \n",
                            "지출번호", "지출타입", "지출금액", "지출발생일");
                    System.out.println("-".repeat(60));

                    for (Expense e : expenseList) {
                        System.out.printf("%5d | %10s | %15,d | %21s \n",
                                e.getEIdx(), e.getEType(), e.getEAmount(), e.getEDate());
                        //System.out.printf("- [%s] %s: %,d원\n", e.getEDate(), e.getEType(), e.getEAmount());
                    }
                    System.out.println("-".repeat(60));
                    System.out.println("\n총 매출: " + String.format("%,d원", totalSales));
                    System.out.println("총 지출: " + String.format("%,d원", totalExpense));
                    System.out.println("총 잔액: " + String.format("%,d원", netAmount));
                }
            }

            case "Sales" -> {
                if (isYear) {
                    Map<String, Map<String, Long>> monthlySummary = (Map<String, Map<String, Long>>) result.get("monthlySummary");
                    long totalSales = (long) result.get("totalSales");

                    System.out.printf("%10s | %14s \n", "월", "매출");
                    System.out.println("-".repeat(60));

                    for (String month : monthlySummary.keySet()) {
                        Map<String, Long> data = monthlySummary.get(month);
                        System.out.printf("%10s | %14,d \n", month, data.get("sales"));
                        //System.out.printf("%s월: 매출 %,d원\n", month, data.get("sales"));
                    }
                    System.out.println("-".repeat(60));
                    System.out.println("\n총 매출: " + String.format("%,d원", totalSales));
                } else {
                    List<Sales> salesList = (List<Sales>) result.get("salesList");
                    long totalSales = (long) result.get("totalSales");

                    System.out.println("[매출 내역]");
                    System.out.printf("%5s | %15s | %21s  \n",
                            "매출번호", "매출금액", "매출발생일");
                    System.out.println("-".repeat(60));
                    for (Sales s : salesList) {
                        System.out.printf("%5d | %15d | %21s \n",
                                s.getSIdx(), s.getSPrice(), s.getSDate());
                        //System.out.printf("- [%s] %,d원\n", s.getSDate(), s.getSPrice());
                    }

                    System.out.println("-".repeat(60));
                    System.out.println("\n총 매출: " + String.format("%,d원", totalSales));
                }
            }

            case "Expense" -> {
                if (isYear) {
                    Map<String, Map<String, Long>> monthlySummary = (Map<String, Map<String, Long>>) result.get("monthlySummary");
                    long totalExpense = (long) result.get("totalExpense");

                    System.out.printf("%10s | %14s \n", "월", "지출");
                    System.out.println("-".repeat(60));

                    for (String month : monthlySummary.keySet()) {
                        Map<String, Long> data = monthlySummary.get(month);
                        System.out.printf("%10s | %14,d \n", month, data.get("expense"));
                        //System.out.printf("%s월: 매출 %,d원\n", month, data.get("sales"));
                    }
                    System.out.println("-".repeat(60));
                    System.out.println("\n총 지출: " + String.format("%,d원", totalExpense));
                } else {
                    List<Expense> expenseList = (List<Expense>) result.get("expenseList");
                    long totalExpense = (long) result.get("totalExpense");

                    System.out.printf("%5s | %10s | %15s | %21s \n",
                            "지출번호", "지출타입", "지출금액", "지출발생일");
                    System.out.println("-".repeat(60));

                    for (Expense e : expenseList) {
                        System.out.printf("%5d | %10s | %15,d | %21s \n",
                                e.getEIdx(), e.getEType(), e.getEAmount(), e.getEDate());
                        //System.out.printf("- [%s] %s: %,d원\n", e.getEDate(), e.getEType(), e.getEAmount());
                    }

                    System.out.println("-".repeat(60));
                    System.out.println("\n총 지출: " + String.format("%,d원", totalExpense));
                }
            }
        }
    }

    private String getFinanceDate(){
        while(true) {
            String num = inputNum("""
                    ============================================================
                                    1. 월 단위  |  2. 연 단위
                    ============================================================
                    >  """);

            //반환할 날짜 변수
            String date;

            String year;
            String month;
            switch (num) {
                    case "1": //월별정산 선택 시
                        year = inputNum("년도>  ");
                        month = inputNum("월>  ");
                        date = year + "-" + month;
                        return date;
                    case "2": //연도별 정산 선택 시
                        year = inputNum("년도>  ");
                        date = year;
                        return date;
                    default:
                        System.out.println("번호를 잘못 입력했습니다.");
            }
        }
    }
    private String getFinanceType(){
        while(true) {
            //메뉴 번호 입력
            String num = inputNum("""
                ============================================================
                    1.내역 조회 | 2.매출 조회 | 3.지출 조회  
                ============================================================
                >  """);

            switch (num) {
                case "1" -> {
                    return "All";
                }
                case "2" -> {
                    return "Sales";
                }
                case "3" -> {
                    return "Expense";
                }
                default -> System.out.println("번호를 잘못 입력했습니다.");
            }
        }
    }

    private String inputNum(String msg){
        System.out.print(msg);
        try {
            return input.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
