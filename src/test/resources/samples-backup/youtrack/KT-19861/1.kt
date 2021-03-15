// Original bug: KT-43937
// Duplicated bug: KT-19861

class ListNode(var `val`: Int) {
	var next: ListNode? = null
}
class Solution {
    fun plusOne(head: ListNode?): ListNode? {
        fun go(node: ListNode?): Boolean {
            if (node == null)
                return false
            var carry = go(node?.next)
            if (carry || node?.next == null) {
                if (++node?.`val` == 10) {  // <-- THIS LINE CAUSES COMPILE ERROR (however, this works ok: ++node!!.`val`)
                    node?.`val` = 0
                    return true
                }
            }
            return false
        }
        if (go(head)) {
            var pre: ListNode? = ListNode(1)
            pre?.next = head
            return pre
        }
        return head
    }
}
