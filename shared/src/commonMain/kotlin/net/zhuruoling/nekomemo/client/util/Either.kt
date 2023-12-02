package net.zhuruoling.nekomemo.client.util


open class Either<T, U> {

    companion object{
        fun <T, U> left(value: T): Either<T, U> {
            return Left(value)
        }

        fun <T, U> right(value: U): Either<T, U> {
            return Right(value)
        }
    }

    class Left<T, U>(val value: T) : Either<T, U>() {
        override fun toString(): String {
            return "Left($value)"
        }
    }

    class Right<T, U>(val value: U) : Either<T, U>() {
        override fun toString(): String {
            return "Right($value)"
        }
    }
}