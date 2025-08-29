<script>
  import { user } from '$lib/stores/user';
  import { goto } from '$app/navigation';

  function logout() {
    user.set(null);
    goto('/login', { replaceState: true });
  }
</script>

<div class="app">
  <!-- 상단 헤더 (공통) -->
  <header class="topbar">
    <div class="logo-wrap">
      <h1 class="logo">YELLOW SOCKS</h1>
    </div>

    <!-- 상단 중앙 문구 유지 -->
    <div class="header-center">
      <span class="subtitle">ASSET MANAGEMENT SYSTEM</span>
      <span class="role-tag">(user)</span> <!-- ✅ 추가 -->
    </div>

    <div class="user-box">
      <span class="username">{$user?.id ?? 'user name'}</span>
      <span class="avatar" aria-hidden="true">👤</span>
      <button class="logout-top" on:click={logout}>logout</button>
    </div>
  </header>

  <!-- 페이지 콘텐츠 -->
  <slot />
</div>

<style>
  :global(html), :global(body){
    margin:0; background:#f1eadb;
    font-family:system-ui, -apple-system, Segoe UI, Roboto, sans-serif;
  }

  :root{
    --gold:#d1a931;
    --gold-soft:#e6cf78;
    --ink:#3a2d12;
    --muted:#7a7a7a;
    --border:#e6e0d0;
    --card:#ffffff;
    --sand:#d9c8a2;
    --sand-2:#e8dec3;
  }

  .app{ min-height:100vh; display:flex; flex-direction:column; }

  .topbar{
    display:grid;
    grid-template-columns: auto 1fr auto;
    align-items:center;
    padding:16px 20px;
    background:#fff;
    border-bottom:2px solid #d9d5c8;
  }
  .logo-wrap{ display:flex; align-items:center; }
  .logo{
    margin:0; font-size:36px; font-weight:900; letter-spacing:.6px;
    background: linear-gradient(90deg, #FFD700, #FFC107, #B8860B);
    -webkit-background-clip:text; -webkit-text-fill-color:transparent;
            background-clip:text;  color:transparent;
  }
  .header-center{ text-align:center; }
  .subtitle{
    font-size:28px; font-weight:700; letter-spacing:.2px; color:#bda760;
  }
  /* ✅ 추가: 작은 (admin) 뱃지 */
  .role-tag{
    margin-left:8px;
    font-size:20px;
    font-weight:700;
    color:#a58a2e; /* 골드 계열, 좀 더 흐릿하게 */
    vertical-align:baseline;
    opacity:.9;
  }

  .user-box{ display:flex; align-items:center; gap:10px; color:#8b7425; justify-self:end; }
  .username{ font-weight:700; }
  .avatar{ font-size:18px; }
  .logout-top{
    border:none; background:transparent; color:#8b7425; cursor:pointer; font-weight:700;
  }
</style>
