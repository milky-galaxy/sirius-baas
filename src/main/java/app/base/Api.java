package app.base;

public interface Api<Req extends ApiRequest, Res extends ApiResponse> {

    /**
     * API入口方法
     */
    Res call(Req param) throws Throwable;

}
