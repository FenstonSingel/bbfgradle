
tailrec fun lux(l: String
): String {
        ""?.let {
            return lux("".drop(1))
        }
}
