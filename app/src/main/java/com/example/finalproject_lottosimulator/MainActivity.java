package com.example.finalproject_lottosimulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject_lottosimulator.databinding.ActivityMainBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;

    int[] winLottoNumArr = new int[6]; //배열의 3번칸에 적힌 값? 0
    int bonusNum = 0;

    List<TextView> winNumTxts = new ArrayList<>();

    long useMoney = 0L;

    long winMoney = 0L;

    int firstRankCount = 0;
    int secondRankCount = 0;
    int thirdRankCount = 0;
    int fourthRankCount = 0;
    int fifthRankCount = 0;
    int unrankedCount = 0;

    List<TextView> myNumTxts = new ArrayList<>();

    boolean isAutoBuyRunning = false;

    Handler mHandler = new Handler();
    Runnable buyLottoRunnable = new Runnable() {
        @Override
        public void run() {
            if(useMoney <10000000){
                makeLottoWinNumbers();
                checkWinRank();
                mHandler.post(buyLottoRunnable);
            }
            else{
                Toast.makeText(mContext, "로또 구매를 종료합니다.", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        setupEvents();
        setValues();

    }

    @Override
    public void setupEvents() {
        binding.buyAutoLottoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isAutoBuyRunning){
                    mHandler.post(buyLottoRunnable);
                    isAutoBuyRunning = true;
                    binding.buyAutoLottoBtn.setText(getResources().getString(R.string.pause_auto_buying));
                }
                else{
                    mHandler.removeCallbacks(buyLottoRunnable);
                    isAutoBuyRunning=false;
                    binding.buyAutoLottoBtn.setText(getResources().getString(R.string.resume_auto_buying));
                }

            }
        });

        binding.buyOneLottoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeLottoWinNumbers();
                checkWinRank();
            }
        });

    }

    @Override
    public void setValues() {
        winNumTxts.add(binding.winNumTxt01);
        winNumTxts.add(binding.winNumTxt02);
        winNumTxts.add(binding.winNumTxt03);
        winNumTxts.add(binding.winNumTxt04);
        winNumTxts.add(binding.winNumTxt05);
        winNumTxts.add(binding.winNumTxt06);

        myNumTxts.add(binding.myNumTxt01);
        myNumTxts.add(binding.myNumTxt02);
        myNumTxts.add(binding.myNumTxt03);
        myNumTxts.add(binding.myNumTxt04);
        myNumTxts.add(binding.myNumTxt05);
        myNumTxts.add(binding.myNumTxt06);
    }

    void checkWinRank(){

//        사용금액은 무조건 1000원 증가
        useMoney += 1000;
//        증가된 사용금액을 화면에 반영 (중복코드)
        binding.useMoneyTxt.setText(String.format("%,d원",useMoney));

//        맞춘 개수를 저장 변수
        int correctCount = 0;

//        내 입력 번호가 적힌 텍스트뷰들을 꺼내봄.
        for(TextView myNumTxt : myNumTxts){

//            적혀있는 숫자를 int로 변경
           int myNum = Integer.parseInt(myNumTxt.getText().toString());
//           내 숫자를 들고 => 당첨번호를 돌면서 확인.
            for(int winNum : winLottoNumArr){
//                같은걸 찾았다면, 맞춘 갯수를 1 증가.
                if(myNum == winNum){
                    correctCount++;
                }

            }
        }

//        맞춘 갯수에 따른 등수 판정 + 당첨금액 누적
        if(correctCount ==6){
            winMoney +=1300000000;
            firstRankCount++;
        }
        else if(correctCount==5){
//            5개를 맞췄을 때, 보너스 번호 여부에 따라 2/3등 갈림.
            boolean isBonusNumCorrect = false;
//            내 입력 번호 텍스트뷰 목록을 돌면서 확인
            for(TextView myNumTxt : myNumTxts){
//                텍스트뷰에 적힌 내용을 int로 변경
                int myNum = Integer.parseInt(myNumTxt.getText().toString());
//                보너스 번호와 비교해서, 같은게 있다면 보너스를 맞췄다고 처리.
//                한 번도 이 분기에 못 들어왔다 => 보너스 못맞춤.
                if(myNum == bonusNum){
                    isBonusNumCorrect = true;
                    break;
                }
            }
            if(isBonusNumCorrect){
                winMoney+=54000000;
                secondRankCount++;
            }
            else{
                winMoney+=1450000;
                thirdRankCount++;
            }
        }
        else if(correctCount ==4){
            winMoney+= 50000;
            fourthRankCount++;
        }
        else if(correctCount ==3){
            useMoney -=5000;
            fifthRankCount++;
        }
        else{
            unrankedCount++;
        }
        binding.winMoneyTxt.setText(String.format("%,d원",winMoney));
        binding.useMoneyTxt.setText(String.format("%,d원",useMoney));

        binding.firstRankTxt.setText(String.format("%,d회", firstRankCount));
        binding.secondRankTxt.setText(String.format("%,d회", secondRankCount));
        binding.thirdRankTxt.setText(String.format("%,d회", thirdRankCount));
        binding.fourthRankTxt.setText(String.format("%,d회", fourthRankCount));
        binding.fifthRankTxt.setText(String.format("%,d회", fifthRankCount));
        binding.unrankedTxt.setText(String.format("%,d회", unrankedCount));

    }

    void makeLottoWinNumbers(){

//        기존 당첨번호를 모두 0으로 세팅
        for(int i=0; i<winLottoNumArr.length; i++){
            winLottoNumArr[i] = 0;
        }
//        보너스 번호도 0으로 세팅
        bonusNum = 0;
//        당첨번호 6개를 뽑기 위한 for
        for(int i=0; i<winLottoNumArr.length; i++){
//            조건에 맞는 (중복이 아닌) 숫자를 뽑을 때까지 무한 반복.
            while(true){
//              1~45중 하나 랜덤
                int randomNum = (int)(Math.random()*45+1);

//                중복검사 결과 저장 변수 => 일단 맞다고 했다가, 수틀리면 false로 변경
                boolean isDuplicatedOk = true;

//                당첨번호중 같은게 있다면 false로 변경.
//                한 번도 같은게 없었다면, true로 유지.

                for(int num : winLottoNumArr){
                    if(num == randomNum){
                        isDuplicatedOk = false;
                        break;
                    }
                }

//                중복 검사가 통과되었다면
                if(isDuplicatedOk){
//                    당첨 번호로 등록
                    winLottoNumArr[i] = randomNum;
//                    무한 반복 탈출 => 다음 당첨번호 뽑으러 감.(for문 다음 i)
                    break;
                }
            }
        }

//        6개를 뽑는 for문이 다 돌고나면 => 순서가 뒤죽박죽
//        Arrays클래스의 static메소드 활용해서 오름차순 정렬.
        Arrays.sort(winLottoNumArr);

//        보너스 번호를 뽑는 무한반복 => 1개만 뽑는다 for문은 없고, 바로 무한반복.
        while (true) {
//            1~45 랜덤추출
            int randomNum = (int)(Math.random()*45+1);
//            중복검사 진행 (앞과 같은 로직)
            boolean isDuplicateOk = true;
            for(int num : winLottoNumArr){
                if(num ==randomNum){
                    isDuplicateOk = false;
                    break;
                }
            }
            if(isDuplicateOk){
                bonusNum = randomNum;
                break;
            }
        }


//        당첨 번호들을 텍스트뷰에 표시.
        for(int i=0; i<winNumTxts.size(); i++){
            int winNum = winLottoNumArr[i];

//            화면에 있는 당첨번호 텍스트뷰들을 ArrayList에 담아두고 활용.
            winNumTxts.get(i).setText(winNum+"");

        }

        binding.bonusNumTxt.setText(bonusNum+"");
    }
}
