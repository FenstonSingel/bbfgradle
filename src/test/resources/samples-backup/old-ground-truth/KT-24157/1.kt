// Original bug: KT-24701
// Duplicated bug: KT-24157

import java.util.concurrent.ExecutorService

inline fun ExecutorService.submitSafe(){
    actual@ submit {
        return@actual
    }
}
