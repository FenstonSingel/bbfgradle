// Original bug: KT-17019
// Duplicated bug: KT-19861

object Test
{
    //var tester : Tester? = Tester()
    var tester2 : Tester2? = Tester2()
    fun logText(message : String)
    {
        //tester?.field += message
        tester2?.field += message
    }

    class Tester2
    {
        var field = "";
    }
}
