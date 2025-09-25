package controller;

import dao.InboundBillVO;
import service.InboundService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* 메뉴 첫 접근
* : BoardController/showBoardMenu(), selectBoardMenu()
* -> 유저번호(uIdx)를 파라미터로 넘겨야 요청 등록에 사용 가능
* [입고 관리]
* */

public class InboundControllerImpl implements InOutboundController{
    // statics
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static SimpleDateFormat informat = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd");



    // 싱글턴 패턴을 위한 인스턴스 생성
    private static InboundControllerImpl inboundControllerImpl;
    private InboundControllerImpl() {}

    // getInstance는 1회만
    public static InboundControllerImpl getInstance() {
        if (inboundControllerImpl == null) {
            inboundControllerImpl = new InboundControllerImpl();
        }
        return inboundControllerImpl;
    }

    private InboundService inboundService = InboundService.getInstance();

    @Override
    public void showMenu(int authNum) {
        int status = 0;
        // 권한 구분 임의 구현..
        if (authNum == 1) {
            System.out.print(
                    """
                    ============================================================
                    1. 입고 요청 승인		  2. 입고 요청 수정		 3. 입고 요청 취소
                    4. 입고 고지서 출력	  5. 입고 현황 조회		 6. 나가기
                    ============================================================
                    메뉴를 고르세요 :\s"""
            );

        } else if (authNum == 2) {
            System.out.print(
                    """
                    ============================================================
                    1. 입고 요청 		  2. 입고 요청 수정		 3. 입고 요청 취소
                    4. 입고 고지서 출력	  5. 입고 현황 조회		 6. 나가기
                    ============================================================
                    메뉴를 고르세요 :\s"""
            );
        }
        try {
            // 메뉴 번호 입력받음
            int menuNum = Integer.parseInt(br.readLine());
            status = selectMenu(authNum, menuNum);
            if (status == 1) {
                showMenu(authNum);
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println(Errors.INVALID_INPUT_ERROR.getText());
            showMenu(authNum);

        } catch (Exception e) {
            System.out.println(Errors.UNEXPECTED_ERROR.getText());
            e.printStackTrace();
            showMenu(authNum);
        }
    }

    @Override
    public int selectMenu(int authNum, int menuNum) {
        switch (menuNum) {
            case 1 -> {
                int status = 0;
                if(authNum == 1) {
                    System.out.println("1. 입고 요청 승인");
                    // 미승인된 입고요청 목록 출력

                    status = inboundService.approveRequest();

                } else if(authNum == 2) {
                    // 1. 입고 요청
                    status = InputRequestData();

                }
                if (status == -1) {
                    System.out.println(Errors.DATA_INPUT_ERROR.getText());

                } else {
                    System.out.printf(
                            """
                            ============================================================
                            입고 요청이 등록되었습니다. 회원님의 요청 번호는 [%d] 입니다.
                            ============================================================
                            """, status
                    );
                }
            }
            case 2 -> {
                // 2. 입고 요청 수정
                showUpdateMenu();

            }
            case 3 -> {
                int status = 0;
                // 3. 출고 요청 취소
                status = cancelRequest();
                if (status == -1) {
                    System.out.println(Errors.DATA_INPUT_ERROR.getText());
                } else {
                    System.out.print(
                            """
                            ============================================================
                            출고 요청 취소가 완료되었습니다.
                            ============================================================
                            """
                    );
                }

            }
            case 4 -> {
                int status = 0;
                // 4. 입고고지서 출력
                status = showRequestInfo();
                if (status == -1) {
                    System.out.println(Errors.DATA_INPUT_ERROR.getText());
                } else {
                    System.out.print(
                            """
                            .
                            .
                            .
                            """
                    );
                }

//                inboundService.showRequestInfo();

            }

            case 5 -> {
                System.out.println("5. 입고 현황 조회");
                inboundService.getBoundInfo();

            }

            case 6 -> {
                System.out.println("6. 나가기");
                return 0;
            }
        }

        return 1;
    }

    @Override
    public void showUpdateMenu() {
        int status = 0;
        System.out.print(
                """
                ============================================================
                ####################### 입고 요청 수정 #######################
                ============================================================
                1. 요청 정보 수정 	    2. 물품 정보 수정           3. 뒤로가기
                ============================================================
                메뉴를 고르세요 :\s"""
        );
        try {
            int menuNum = Integer.parseInt(br.readLine());
            status = selectUpdateMenu(menuNum);
            if (status == 1) {
                System.out.print("""
            ============================================================
            수정이 완료되었습니다.
            ============================================================
            """);
                showUpdateMenu();
            } else if (status == -1) {
                System.out.println(Errors.DATA_INPUT_ERROR.getText());
                showUpdateMenu();
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println(Errors.INVALID_INPUT_ERROR.getText());
            showUpdateMenu();

        } catch (Exception e) {
            System.out.println(Errors.UNEXPECTED_ERROR.getText());
            e.printStackTrace();
            showUpdateMenu();
        }
    }

    @Override
    public int selectUpdateMenu(int menuNum) {
        int status = 0;
        try {
            switch (menuNum) {
                // 요청 정보 수정
                case 1 -> {
                    // 요청 번호 입력
                    System.out.print(
                            """
                            ============================================================
                            수정할 입고요청 번호를 입력해주세요.
                            요청번호 :\s"""
                    );
                    int requestId = Integer.parseInt(br.readLine());
                    status = InputRequestDataUpdate(requestId);
                    if (status == -1) {
                        return -1;
                    }
                }
                // 물품 정보 수정
                case 2 -> {
                    // 요청 번호 입력
                    System.out.print(
                            """
                            ============================================================
                            수정할 입고요청 번호를 입력해주세요.
                            요청번호 :\s"""
                    );
                    int requestId = Integer.parseInt(br.readLine());
                    status = InputRequestItemUpdate(requestId);
                    if (status == -1) {
                        return -1;
                    }

                }
                case 3 -> {
                    // 뒤로가기
                    return 0;
                }
            }
        } catch (IOException e) {
            System.out.println(Errors.INVALID_INPUT_ERROR.getText());
        }
        return 1;
    }

    @Override
    public void showInfoMenu() {

    }

    @Override
    public int selectInfoMenu(int menuNum) {
        return 0;
    }


    /*
    * ---------------------서브 메서드----------------------
    * */

    // 입고 요청 정보를 서비스에 보내기
    // 창고번호 int, 입고기한 date -> 물품번호 , 물품개수
    public int InputRequestData() {
        int rtn = 0;
        try {
            System.out.print(
                    """
                    ============================================================
                    ######################### 입고 요청  #########################
                    ============================================================
                    요청 정보를 작성해주세요.
                    입고위치(창고번호) :\s"""
            );
            int wId = Integer.parseInt(br.readLine());

            System.out.print(
                    """
                    ============================================================
                    입고기한(8자리 숫자로 입력) :\s"""
            );
            String dueDate = br.readLine();
            Date date = informat.parse(dueDate);
            String newDueDate = outformat.format(date);
            // 요청 정보 전송
            int requestStatus = inboundService.addRequest(wId, newDueDate);

            // 실행 결과 오류 검증
            if (requestStatus == -1) {
                return -1;
            }

            while (true) {
                System.out.print(
                        """
                        ============================================================
                        물품 정보를 작성해주세요.
                        물품번호 :\s"""
                );
                String productId = br.readLine();

                System.out.print(
                        """
                        ============================================================
                        물품개수 :\s"""
                );
                int productQuantity = Integer.parseInt(br.readLine());
                // 물품 정보 전송
                int itemStatus = inboundService.addRequest(productId, productQuantity);

                // 실행 결과 오류 검증
                if (itemStatus == -1) {
                    return -1;
                }

                System.out.print(
                        """
                        ============================================================
                        물품 정보가 정상적으로 입력되었습니다.
                        ============================================================
                        Q를 입력하면 메뉴 화면으로 이동하며,
                        물품 정보를 추가로 입력하려면 Q를 제외한 아무 키나 입력하십시오.
                        :\s"""
                );
                String select = br.readLine().toUpperCase();
                if (select.charAt(0) == 'Q') {
                    rtn = requestStatus;
                    break;
                }
            }

        } catch (IOException e) {
            // 입력오류
            System.out.println(Errors.INVALID_INPUT_ERROR.getText());
        } catch (ParseException e) {
            e.printStackTrace();
            InputRequestData();
        }
        return rtn;
    }

    // 요청 정보 수정 입력
    public int InputRequestDataUpdate(int requestId) {
        int rtn = 0;

        try {
            System.out.print(
                    """
                    ============================================================
                    수정할 내용을 입력해주세요.
                    창고번호 :\s"""
            );
            int wId = Integer.parseInt(br.readLine());

            System.out.print(
                    """
                    ============================================================
                    입고기한(8자리 숫자로 입력) :\s"""
            );
            String dueDate = br.readLine();
            Date date = informat.parse(dueDate);
            String newDueDate = outformat.format(date);

            // 요청 정보 전송
            int requestStatus = inboundService.updateRequest(requestId, wId, newDueDate);
            if (requestStatus == -1) {
                rtn = -1;
            }

        } catch (IOException | NumberFormatException | ParseException e) {
            return -1;
        }
        return rtn;
    }

    // 요청 물품 수정 입력
    public int InputRequestItemUpdate(int requestId) {
        int rtn = 0;

        try {
            System.out.print(
                    """
                    ============================================================
                    수정할 물품 순번을 입력해주세요.
                    순번 :\s"""
            );
            int itemId = Integer.parseInt(br.readLine());

            System.out.print(
                    """
                    ============================================================
                    물품 정보를 작성해주세요.
                    물품번호 :\s"""
            );
            String productId = br.readLine();

            System.out.print(
                    """
                    ============================================================
                    물품개수 :\s"""
            );
            int productQuantity = Integer.parseInt(br.readLine());

            // 요청 정보 전송
            int requestStatus = inboundService.updateItem(requestId, itemId, productId, productQuantity);
            if (requestStatus == -1) {
                rtn = -1;
            }

        } catch (IOException | NumberFormatException e) {
            return -1;
        }

        return rtn;
    }

    public int cancelRequest() {
        int rtn = 0;
        try {
            System.out.print(
                    """
                    ============================================================
                    ####################### 입고 요청 취소 #######################
                    ============================================================
                    취소할 입고요청 번호를 입력해주세요.
                    요청번호 :\s"""
            );
            int requestId = Integer.parseInt(br.readLine());

            // 취소 확인
            System.out.print(
                    """
                    ============================================================
                    입고 요청을 취소하시겠습니까?
                    (Y/N 입력) :\s"""
            );
            String select = br.readLine().toUpperCase();
            if (select.charAt(0) == 'N') {
                System.out.print("""
                    ============================================================
                    메뉴 화면으로 이동합니다.
                    ============================================================
                    """);
            } else if (select.charAt(0) == 'Y') {
                // 요청 정보 전송
                int requestStatus = inboundService.cancelRequest(requestId);
                if (requestStatus == -1) {
                    rtn = -1;
                }
            } else {
                throw new IOException();
            }

        } catch (IOException e) {
            System.out.println(Errors.INVALID_INPUT_ERROR.getText());
            return -1;
        }
        return rtn;
    }

    public int showRequestInfo() {
        int rtn = 0;
        try {
            System.out.print(
                    """
                    ============================================================
                    ####################### 입고고지서 출력 #######################
                    ============================================================
                    출력할 입고요청 번호를 입력해주세요.
                    요청번호 :\s"""
            );
            int requestId = Integer.parseInt(br.readLine());

            // 출력 확인
            System.out.print(
                    """
                    ============================================================
                    해당 입고요청의 입고고지서를 출력하시겠습니까?
                    (Y/N 입력) :\s"""
            );
            String select = br.readLine().toUpperCase();
            if (select.charAt(0) == 'N') {
                System.out.print("""
                    ============================================================
                    메뉴 화면으로 이동합니다.
                    ============================================================
                    """);
            } else if (select.charAt(0) == 'Y') {
                // 요청 정보 전송
                InboundBillVO vo = inboundService.showReqBillData(requestId);
                List<List<String>> list = inboundService.showItemBillData(requestId);

                if ((vo == null) || (list == null)) {
                    rtn = -1;
                } else {
                    printBill(vo, list);
                    System.out.print("""
                    ============================================================
                    아무 키나 누르면 입고 관리 메뉴로 돌아갑니다.
                    :\s""");
                    String input = br.readLine();

                }
            } else {
                throw new IOException();
            }

        } catch (IOException e) {
            System.out.println(Errors.INVALID_INPUT_ERROR.getText());
            return -1;
        }
        return rtn;
    }

    // 입고고지서 양식대로 출력
    public void printBill(InboundBillVO vo, List<List<String>> list) {

        System.out.printf(
                """
                ============================================================
                 요청번호 |   입고일자   | 창고위치 |               |   요청자   |
                ------------------------------------------------------------
                 %4d      %10s    %2d                        %-8s
                
                  순번   |            물품이름            |  수량  |   단가    |
                """, vo.getInRequestId(), outformat.format(vo.getInDate()), vo.getWId(), vo.getUName()
        );
        int totalPrice = 0;

        for (List<String> item : list) {
            System.out.printf("""
                %4s  %25s\t   \t%4s  %10s
                """, item.get(0), item.get(1), item.get(2), item.get(3)
            );
            totalPrice += (Integer.parseInt(item.get(2)) * Integer.parseInt(item.get(3)));
        }

        System.out.printf("""
                                                                 | 총 금액  |
                                                              %10d
                """, totalPrice);
    }

}
