import { createRouter, createWebHistory } from 'vue-router'
import Landing from '../views/Landing.vue'
import Player from '../views/Player.vue'

const routes = [
  {
    path: '/',
    name: 'Landing',
    component: Landing
  },
  {
    path: '/index',
    name: 'Player',
    component: Player
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router


