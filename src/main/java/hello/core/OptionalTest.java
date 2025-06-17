package hello.core;

import java.util.Optional;

public class OptionalTest {

    public static void main(String[] args) {
        String str = null;
        String str1 = "";
        System.out.println(str1.length());
        //System.out.println(str.length());


        //Optional 객체 생성하기
        String strr = "ggggggggggggg";
        Optional<String> optVal = Optional.of(strr);
        Optional<String> optVal2 = Optional.of("abc");
        Optional<String> optVal3 = Optional.ofNullable(null);

        //null일수 있는 값은 Optional을 통해서 쓰자
        //null 대신 빈 Optional 객체를 사용하자
        Optional<String> optVallNull = Optional.empty();

        //객체 가져 오기
        // get(), orElse(), orElseGet(), orElseThorw()
        String str2 = optVal.get(); // optVal에 저장된 값을 반환, null 이면 예외 발생
        String str3 = optVal.orElse(""); // "" 는 저장된 값이 null일때 ""를 반환
        String str4 = optVal.orElseGet(String::new);  //(Supplier<? extends T>)
        // String str5 = optVal.orElseGet(()->new String(""));
        String str5 = optVal.orElseThrow(NullPointerException::new); // null 이면 예외 발생

        // isPresent() - Optional 객체의 값이 null 이면 false 아니면 true반환
        if (Optional.ofNullable(strr).isPresent()) {
            System.out.println(strr);
        }

        // ifPresent() - 널이 아닐때만 작업 수행, null 이면 수행하지 않음
        Optional.ofNullable(strr).ifPresent(System.out::println);


        
        //Optional<String> test = null; 가능하지만 바람지 하지 않음
        Optional<String> test = Optional.empty();
        System.out.println("test = " + test);
        //System.out.println("test = " + test.get());
        System.out.println("test = " + test.orElseGet(()-> new String("EMPTYs")));

        //OptionalInt, OptionalLong, OptionalDouble
        //getAsInt(), getAsLong(), getAsDouble()
        // OptionalInt.of(0), OptionalInit.empty(); > 모두 0을 저장
        // boolean isPresent() > 0 값을 가지고 있는것과 빈값(0)이 있는경우를 구분하기 위해


    }
}
