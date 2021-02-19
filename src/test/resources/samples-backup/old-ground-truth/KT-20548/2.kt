// Original bug: KT-20548
// Duplicated bug: KT-20548

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@interface Int {
    Class value();
}

@Int(String.class)
public class Test {
}
