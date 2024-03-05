import Instant from "./time/Instant";
import LocalDate from "./time/LocalDate";
import LocalDateTime from "./time/LocalDateTime";
import LocalTime from "./time/LocalTime";
import LunarDate from "./time/LunarDate";
import OffsetDateTime from "./time/OffsetDateTime";
import OffsetTime from "./time/OffsetTime";
import ZonedDateTime from "./time/ZonedDateTime";

export const typeMap = {
    "Instant": Instant.TYPE,
    "LocalDate": LocalDate.TYPE,
    "LocalDateTime": LocalDateTime.TYPE,
    "LocalTime": LocalTime.TYPE,
    "LunarDate": LunarDate.TYPE,
    "OffsetDateTime": OffsetDateTime.TYPE,
    "OffsetTime": OffsetTime.TYPE,
    "ZonedDateTime": ZonedDateTime.TYPE,

    "Instant.TYPE": Instant.TYPE,
    "LocalDate.TYPE": LocalDate.TYPE,
    "LocalDateTime.TYPE": LocalDateTime.TYPE,
    "LocalTime.TYPE": LocalTime.TYPE,
    "LunarDate.TYPE": LunarDate.TYPE,
    "OffsetDateTime.TYPE": OffsetDateTime.TYPE,
    "OffsetTime.TYPE": OffsetTime.TYPE,
    "ZonedDateTime.TYPE": ZonedDateTime.TYPE,
};
