/**********************************************************
 이 클래스는 열차 정보를 저장하고 형식을 변경하는 데에 사용된다
 ***********************************************************/
package sogong.train.info;

public class TrainInfo {
    private String arrTime;     // 도착 시간
    private String depTime;     // 출발 시간
    private String trainName;   // 열차 이름

    public TrainInfo(String arrTime, String depTime, String trainName) {
        this.arrTime = arrTime;
        this.depTime = depTime;
        this.trainName = trainName;
    }

    // 시간 형식을 HH:MM으로 변환하는 메서드
    // Ex) 0930 -> 09:30
    public String formatTime(String time) {
        if (time != null && time.length() == 4) {
            return time.substring(0, 2) + ":" + time.substring(2, 4);
        }
        return time;
    }

    // 드롭박스 UI에서 열차 정보를 표시하기 위한 문자열 생성
    public String toDropboxFormat() {
        return formatTime(depTime) + " ~ " + formatTime(arrTime) + ", " + trainName + "번 열차";
    }

    // 디버깅용 toString 메서드
    @Override
    public String toString() {
        return "TrainInfo{" +
                "arrTime='" + arrTime + '\'' +
                ", depTime='" + depTime + '\'' +
                ", trainName='" + trainName + '\'' +
                '}';
    }
}
