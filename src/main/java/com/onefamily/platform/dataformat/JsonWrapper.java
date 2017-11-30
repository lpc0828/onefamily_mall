package com.onefamily.platform.dataformat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JsonWrapper<T> {

    @JsonIgnore
    private static final ObjectMapper mapper = new ObjectMapper();
    @JsonIgnore
    private static final String DateFormat = "yyMMdd HHmmss.SSS";
    @JsonIgnore
    private static final SimpleDateFormat DFT = new SimpleDateFormat("yyMMdd\'T\'HHmmss.SSS\'Z\'");
    @JsonIgnore
    private long stopWatch = System.currentTimeMillis();

    private String version = "1.0";
    private String timestamp = DateTime.now().toString(DateFormat);
    private int status;
    private String errorMsg;
    private long elapsed;
    private String trackId;
    private T data;

    public JsonWrapper() {
        this.status = StatusEnum.UnknowError.getCode();
        this.errorMsg = StatusEnum.UnknowError.getDesc();
        this.elapsed = 0L;
        this.data = null;
    }

    public static JsonWrapper<?> parsingObject(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        return null == jsonString? null : (JsonWrapper)mapper.readValue(jsonString, JsonWrapper.class);
    }

    public String toJsonString() {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(DFT);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "toJsonString error ";
        }
    }

    public long timeWatchStop() {
        this.elapsed = System.currentTimeMillis() - this.stopWatch;
        return this.elapsed;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static enum StatusEnum {
        Success(0, "全部成功"),
        Fail(41, "处理失败"),
        AuthFail(403, "鉴权失败"),
        UnknowError(99, "未知错误");

        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        private StatusEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

}
