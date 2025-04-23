import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

// 使用类型断言解决导入问题
const SysHome = () => import("@/views/SysHome.vue");
const EmployeeLogin = () => import("@/views/EmployeeLogin.vue");
const ManagerLogin = () => import("@/views/ManagerLogin.vue");

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: SysHome, // 主页
  },
  {
    path: '/manager-login', // 管理员登录页面的路径
    name: 'ManagerLogin',
    component: ManagerLogin, // 管理员登录页面
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/manager/AdminFront.vue'),
    children: [
      {
        path: 'adminHome',
        component: () => import('@/views/manager/AdminHome.vue'),
      },
      {
        path: 'notice',
        component: () => import('@/views/manager/components/Notice.vue'),
      },
      {
        path: 'userManage',
        component: () => import('@/views/manager/components/UserManage.vue'),
      },
      {
        path: 'feedback',
        component: () => import('@/views/manager/components/Feedback.vue'),
      },
      {
        path: 'departmentDetails',
        component: () => import('@/views/manager/components/DepartmentDetails.vue'),
      },
      {
        path: 'doctor',
        component: () => import('@/views/manager/components/Doctor.vue'),
      },
      {
        path: 'departments',
        component: () => import('@/views/manager/components/departments.vue'),
      },
      {
        path: 'testPaper',
        component: () => import('@/views/manager/components/TestPaper.vue'),
      },
      {
        path: 'propagate',
        component: () => import('@/views/manager/components/Propagate.vue'),
      },
      {
        path: 'sideshow',
        component: () => import('@/views/manager/components/Sideshow.vue'),
      },
      {
        path: 'topic',
        component: () => import('@/views/manager/components/Topic.vue'),
      },
      {
        path: 'type',
        component: () => import('@/views/manager/components/Type.vue'),
      },
      {
        path: 'doctorAuthentication',
        component: () => import('@/views/manager/components/DoctorAuthentication.vue'),
      },
      {
        path:'person',
        component:()=>import('@/views/manager/components/Person.vue')
      },
      {
        path:'password',
        component:()=>import('@/views/manager/components/Password.vue')
      },
      {
        path:'testRecord',
        component:()=>import('@/views/manager/components/TestRecord.vue')
      }
    ]
  },
  {
    path: '/doctor',
    name: 'Doctor',
    component: () => import('@/views/manager/DoctorFront.vue'),
    children: [
      {
        path: 'propagateDetail/:id',
        component: () => import('@/views/manager/components/PropagateDetail.vue'),
      },
      {
        path: 'propagateDetail',
        component: () => import('@/views/manager/components/PropagateDetail.vue'),
      },
      {
        path: 'doctorPropagate',
        component: () => import('@/views/manager/components/DoctorPropagate.vue'),
      },
      {
        path: 'propagate',
        component: () => import('@/views/manager/components/Propagate.vue'),
      },
      {path:'person',
        component:()=>import('@/views/manager/components/Person.vue')
      },
      {path:'password',
        component:()=>import('@/views/manager/components/Password.vue')
      },
      {
        path: 'doctorAuthentication',
        component: () => import('@/views/manager/components/DoctorAuthentication.vue'),
      },
      {
        path:'topic',
        component:()=>import('@/views/manager/components/Topic.vue')
      },
      {
        path:'testRecord',
        component:()=>import('@/views/manager/components/TestRecord.vue')
      },
      {
        path:'testPaper',
        component:()=>import('@/views/manager/components/TestPaper.vue')
      },
      {
        path:'reservation',
        component:()=>import('@/views/manager/components/Reservation.vue')
      },
      {
        path:'doctorHome',
        component:()=>import('@/views/manager/DoctorHome.vue')
      },
      {
        path:'diagnosis',
        component:()=>import('@/views/manager/components/Diagnosis.vue')
      }
        
    ]
  },
  {
    path: '/employee-login',
    name: 'EmployeeLogin',
    component: EmployeeLogin,
  },
  {
    path: '/employee',
    component: () => import('@/views/EmployeeFront.vue'),
    children: [
      {
        path: 'Home',
        component: () => import('@/views/employee/Home.vue'),
      },
      {
        path: 'reservation',
        component: () => import('@/views/employee/Reservation.vue'),
      },
      {
        path: 'propagateDetail',
        component: () => import('@/views/employee/PropagateDetail.vue'),
      },
      {
        path: 'propagate',
        component: () => import('@/views/employee/Propagate.vue'),
      },
      {
        path: 'testPaper',
        component: () => import('@/views/employee/TestPaper.vue'),
      },
      {
        path: 'testRecord',
        component: () => import('@/views/employee/TestRecord.vue'),
      },
      {
        path: 'testPaperDetail',
        component: () => import('@/views/employee/TestPaperDetail.vue'),
      },
      {
        path: 'chat',
        name: 'chat',
        component: () => import('@/components/commonCon.vue'),
        children: [
          {
            path: ':id',
            name: 'ai',
            component: () => import('@/views/employee/Chat.vue'),
          }
        ]
      },
      {
        path: 'feedback',
        component: () => import('@/views/employee/Feedback.vue'),
      },
      {
        path:'myFeedback',
        component:()=>import('@/views/employee/MyFeedback.vue')
      },
      {
        path:'person',
        component:()=>import('@/views/employee/Person.vue')
      },
      {
        path:'personalFile',
        component:()=>import('@/views/employee/PersonalFile.vue') 
      },
      {
        path:'password',
        component:()=>import('@/views/employee/Password.vue')
      }
    ]
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;