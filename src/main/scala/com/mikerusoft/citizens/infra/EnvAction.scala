package com.mikerusoft.citizens.infra

case class EnvAction[E, A](run: E => A) {

  def map[B](func: A => B): EnvAction[E, B] = EnvAction(e => func(run(e)))

  def flatMap[B](func: A => EnvAction[E, B]): EnvAction[E, B] = EnvAction(e => func(run(e)).run(e))

}
