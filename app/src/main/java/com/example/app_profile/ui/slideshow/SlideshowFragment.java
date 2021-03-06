package com.example.app_profile.ui.slideshow;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_profile.MainActivity;
import com.example.app_profile.R;
import com.example.app_profile.Room.AppDatabase_dday;
import com.example.app_profile.Room.AppDatabase_levelcnt;
import com.example.app_profile.Room.User_dday;

import java.util.Calendar;
import java.util.Locale;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    AppDatabase_dday db;
    int dateEndY, dateEndM, dateEndD;
    int ddayValue = 0;
    private AppDatabase_levelcnt db2;

    // 현재 날짜를 알기 위해 사용
    Calendar calendar;
    int currentYear, currentMonth, currentDay;

    // Millisecond 형태의 하루(24 시간)
    private final int ONE_DAY = 24 * 60 * 60 * 1000;

    private static final int REQUEST_CODE = 0;
    private static int PICK_IMAGE_REQUEST = 1;

    TextView edit_endDateBtn, edit_result;
    Button datePicker;
    Button imageChange;
    ImageView imageView;
    Uri dataUri;
    String dataStr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // 시작일, 종료일 데이터 저장
        calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = (calendar.get(Calendar.MONTH));
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker = (Button) root.findViewById(R.id.datePicker);
        edit_endDateBtn = (TextView) root.findViewById(R.id.edit_endDateBtn);
        edit_result = (TextView) root.findViewById(R.id.edit_result);
        imageChange = (Button) root.findViewById(R.id.imageChange);
        imageView = (ImageView) root.findViewById(R.id.imageView);

        db = AppDatabase_dday.getInstance(getContext());
        db2 = AppDatabase_levelcnt.getInstance(this.getContext());
        int size = db.userDao().getDataCount();
        if (size > 0) {
            User_dday day = db.userDao().getAll().get(size-1);
            edit_endDateBtn.setText(day.getDyear() + "년 " + (day.getDmonth() + 1) + "월 " + day.getDdate() + "일");

            edit_result.setText(getDday(day.getDyear(), day.getDmonth(), day.getDdate()));
        }

        // 한국어 설정 (ex: date picker)
        Locale.setDefault(Locale.KOREAN);

        // 디데이 날짜 입력
        // edit_endDateBtn.setText(currentYear + "년 " + (currentMonth + 1) + "월 " + currentDay + "일");

        // datePicker : 디데이 날짜 입력하는 버튼, 클릭시 DatePickerDialog 띄우기
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), endDateSetListener, (currentYear), (currentMonth), currentDay).show();
            }
        });


        if(db2.userDao().getDataCount()<2){
            imageChange.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }
        else{
            imageChange.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);

            // 프로필 사진 변경 버튼
            imageChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
            });
        }


        return root;
    }
    
    // 프로필 사진 선택
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) { // 사진이 선택될 경우에만
            dataUri = data.getData();
            imageView.setImageURI(dataUri);
            dataStr = dataUri.toString();
            Toast.makeText(getActivity(),"사진 변경에 시간이 다소 소요됩니다",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this.getActivity(), MainActivity.class);
            intent.putExtra("dataStr",dataStr);
            startActivity(intent);
        }
    }
    
    /** @brief endDateSetListener
     *  @date 2016-02-18
     *  @detail DatePickerDialog 띄우기, 종료일 저장, 기존에 입력한 값이 있으면 해당 데이터 설정 후 띄우기
     */
    private DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            edit_endDateBtn.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");

            ddayValue = ddayResult_int(dateEndY, dateEndM, dateEndD);

            edit_result.setText(getDday(year, monthOfYear, dayOfMonth));

            User_dday memo = new User_dday(year, monthOfYear, dayOfMonth);
            db.userDao().insert(memo);
        }
    };

    /** @brief getDday
     *  @date 2016-02-18
     *  @param mYear : 설정한 디데이 year, mMonthOfYear : 설정한 디데이 MonthOfYear, mDayOfMonth : 설정한 디데이 DayOfMonth
     *  @detail D-day 반환
     */
    private String getDday(int mYear, int mMonthOfYear, int mDayOfMonth) {

        // D-day 설정
        final Calendar ddayCalendar = Calendar.getInstance();
        ddayCalendar.set(mYear, mMonthOfYear, mDayOfMonth);

        // D-day 를 구하기 위해 millisecond 로 환산하여 d-day 에서 today 의 차를 구한다.
        final long dday = ddayCalendar.getTimeInMillis() / ONE_DAY;
        final long today = Calendar.getInstance().getTimeInMillis() / ONE_DAY;
        long result = dday - today;

        // 출력 시 d-day 에 맞게 표시
        String strFormat;
        if (result > 0) {
            strFormat = "D-%d";
        } else if (result == 0) {
            strFormat = "Today";
        } else {
            result *= -1;
            strFormat = "D+%d";
        }

        final String strCount = (String.format(strFormat, result));

        return strCount;
    }

    public int ddayResult_int(int dateEndY, int dateEndM, int dateEndD) {
        int result = 0;

        result = onCalculatorDate(dateEndY, dateEndM, dateEndD);

        return result;
    }

    /** @brief onPhotoDialog
     *  @date 2016-02-18
     *  @detail 디데이 값 계산
     *  */
    public int onCalculatorDate (int dateEndY, int dateEndM, int dateEndD) {
        try {
            Calendar today = Calendar.getInstance(); // 현재 오늘 날짜
            Calendar dday = Calendar.getInstance();

            // 시작일, 종료일 데이터 저장
            Calendar calendar = Calendar.getInstance();
            int cyear = calendar.get(Calendar.YEAR);
            int cmonth = (calendar.get(Calendar.MONTH) + 1);
            int cday = calendar.get(Calendar.DAY_OF_MONTH);

            today.set(cyear, cmonth, cday);
            dday.set(dateEndY, dateEndM, dateEndD); // D-day의 날짜를 입력합니다.

            long day = dday.getTimeInMillis() / 86400000;
            // 각각 날의 시간 값을 얻어온 후
            // ( 1일의 값(86400000 = 24시간 * 60분 * 60초 * 1000(1초값) ) )

            long tday = today.getTimeInMillis() / 86400000;
            long count = tday - day; // 오늘 날짜에서 dday 날짜를 빼주게 됩니다.
            return (int) count; // 날짜는 하루 + 시켜줘야 합니다.
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
