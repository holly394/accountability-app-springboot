import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    component: () => import('pages/LoginPage.vue')
  },

  {
    path: '/registration',
    component: () => import('pages/RegisterPage.vue')
  },

  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/IndexPage.vue'),
      },
      {
        path: '/user',
        component: () => import('pages/UserPage.vue')
      },
      {
        path: '/tasks',
        component: () => import('pages/TaskPage.vue')
      },
      {
        path: '/wallet',
        component: () => import('pages/WalletPage.vue')
      },
      {
        path: '/relationships',
        component: () => import('pages/RelationshipPage.vue')
      },
      {
        path: '/partner-tasks',
        component: () => import('pages/PartnerPage.vue')
      },
      {
        path: '/partner-wallet-graph',
        component: () => import('components/charts/PartnerWalletsGraph.vue')
      },
      {
        path: '/pagination-test-page',
        component: () => import('pages/PaginationTestPage.vue')
      }
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
