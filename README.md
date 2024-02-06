# Spring Exception Handling

서블릿에서 지원하는 예외 처리 2가지 방식

1. Exception
2. response.setError(HTTP Status Code, Error Message)

spring-boot가 제공하는 error page는 off로 설정(application.yml)

→ 서블릿 error page가 출력됨

```yaml
server:
  error:
    whitelabel:
      enabled: off
```

filter → Servlet Container에서 동작하는 Request, Response에 대한 처리

Interceptor → Spring Framwork에서 동작하며 DispatcherServlet → Controller의 Request, Response에 대한 처리

Interceptor → error(exception) 발생시 preHandle은 호출되고 , postHandle은 호출안됨, afterCompletion은 항상 호출

---

스프링에서 제공하는 오류 페이지

스프링에서는 WebSeverCustomizer, 예외 종류에 따른 ErrorPage와 ErrorPageController를 모두 기본으로 제공한다.

ErrorPage를 자동으로 등록해서, /error 경로로 기본 오류 페이지를 설정

오류, 에러로 WAS까지 와서 다른 error page 설정이 안되어 있으면 기본 오류페이지로 오게됨(defalut)

BasicErrorController라는 ErrorPage에서 등록한 /error로 매핑된 스프링 컨트롤러를 자동으로 등록

⇒ ErrorMvcAutoConfiguration이라는 클래스가 오류 페이지를 자동으로 등록하는 역할을 담당

BasicErrorController의 처리 순서(뷰 선택 우선순위)

1. 뷰 템플릿 ⇒ resources/templates/error/500.html
2. 정적 리소스(static, public) ⇒ resources/static/error/500.html
3. 적용 대상이 없을때 뷰 이름(error) ⇒ resources/templates/error.html

실무에서는 오류 정보를 노출하면 안된다. 사용자에게는 미리 정의한 오류 화면과 고객이 이해할 수 있는 간단한 오류 메시지를 보여주고 **오류는 서버에 로그로 남겨서 로그로 확인**해야 한다.

---

API는 각 오류 상황에 맞는 오류 응답 스펙을 정하고, JSON 데이터를 반환해야 한다.

Controlller →  `produces = MediaType.*APPLICATION_JSON_VALUE`* 설정을 통해  Client가 JSON TYPE으로 요청한 데이터가 매핑된다.

API도 BasicErrorController에서 제공하는 기본 기능이 있다.

Client가 html, json을 요청하는 것에 따라 미리 응답에 대한 구현이 되어 있다.

text/html인 경우에는 ModelAndView, 그 외에는 ResponseEntity를 반환하도록 BasicErrorController에 구현되어 있다.

---

모든 것을 아우르는 대망의 HandlerExceptionResolver

예외가 생기면 WAS까지 예외가 뎐저지고 요청과 응답의 과정이 다소 복잡하다. 그래서 ExceptionResolver을 활용하면 예외가 발생했을때 복잡한 과정없이 문제를 깔끔하게 해결할 수 있다.

예를 들어 IllegalArgumentException을 처리하지 못해 컨트롤러 밖으로 넘어가는 일이 발생하면 HTTP 상태코드를 400으로 처리하고 싶다면?? ⇒  HandlerExceptionResolver를 사용하자.

```
1. 예외가 서블릿을 넘어 WAS까지 예외가 전달되면 HTTP  상태코드가 500으로 처리된다.
2. 발생하는 예외에 따라 세부적인 상태코드도 처리하고 싶다.
3. 오류 메시지, 형식등을 API마다 다르게 처리하고 싶다.
```

** 참고 **

개발자가 별도로 WebMvcConfigurer에서 HandlerExceptionResolver를 등록할 수 있다.

그러나, configureHandlerExceptionResolvers(..) 를 사용하면 스프링이 기본으로 등록하는
ExceptionResolver 가 제거되므로 주의 ⇒ extendHandlerExceptionResolvers를 사용해야함.

오류 요청 흐름 정리

1. WAS(/error-ex, dispatchType=REQUEST) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러
2. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
3. WAS 오류 페이지 확인
4. WAS(/error-page/500, dispatchType=ERROR) -> 필터(x) -> 서블릿 -> 인터셉터(x) -> 컨트
   롤러(/error-page/500) -> View (필터는 디스페쳐 타입으로, 인터셉터는 익스클루드패턴스에 경로를 추가함으로 생략 가능하다)