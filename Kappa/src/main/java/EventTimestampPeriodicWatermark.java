import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

class EventTimestampPeriodicWatermarks implements AssignerWithPeriodicWatermarks<CountedEvent> {
    private long currentMaxTimestamp;

    @Override
    public Watermark getCurrentWatermark() {
        return new Watermark(currentMaxTimestamp);
    }

    @Override
    public long extractTimestamp(CountedEvent countedEvent, long l) {
        long timestamp = countedEvent.timestamp;
        currentMaxTimestamp = Math.max(currentMaxTimestamp, timestamp);
        return timestamp;
    }
}