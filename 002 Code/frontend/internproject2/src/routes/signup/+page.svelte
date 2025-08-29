<!-- src/routes/sign-up/+page.svelte -->
<script>
  import { goto } from '$app/navigation';

  // ✔ 폼 상태 (user name 입력칸 없음)
  let userId = '';     // = customer_name
  let password = '';   // = customer_id (PK/유니크)
  let role = '';       // 'admin' | 'user'  → 저장 시 'admin' | 'borrower'
  let loading = false;
  let errorMsg = '';
  let okMsg = '';

  // 백엔드 (Vite 프록시 X)
  const API_BASE = 'http://localhost:8081';

  function choose(r) {
    role = r;   // 'admin' | 'user'
    errorMsg = '';
    okMsg = '';
  }

  // 이미 존재하는 customer_id(=password)인지 사전 체크
  async function isCustomerIdTaken(customerId) {
    const res = await fetch(`${API_BASE}/api/members/${encodeURIComponent(customerId)}`, {
      method: 'GET',
      headers: { Accept: 'application/json' }
    });
    if (res.ok) return true;              // 200 → 이미 존재
    if (res.status === 404) return false; // 404 → 사용 가능
    throw new Error(`GET /api/members/{id} -> ${res.status}`);
  }

  // ★ 추가: 이미 존재하는 customer_name(=userId)인지 사전 체크
  async function isCustomerNameTaken(customerName) {
    const qs = new URLSearchParams({ customerName: customerName ?? '' }).toString();
    const res = await fetch(`${API_BASE}/api/members/search?${qs}`, {
      method: 'GET',
      headers: { Accept: 'application/json' }
    });
    if (!res.ok) throw new Error(`GET /api/members/search -> ${res.status}`);
    const list = await res.json();
    const target = String(customerName ?? '').trim();
    return Array.isArray(list) && list.some(m => String(m?.customerName ?? '').trim() === target);
  }

  async function onSignUp(e) {
    e.preventDefault();
    errorMsg = '';
    okMsg = '';

    if (!userId || !password) {
      errorMsg = 'ID와 Password를 모두 입력해 주세요.';
      return;
    }
    if (!role) {
      errorMsg = '역할(Admin 또는 User)을 선택해 주세요.';
      return;
    }

    // 역할 매핑 (요구사항: admin | borrower 저장)
    const customerType = role === 'admin' ? 'admin' : 'borrower';

    try {
      loading = true;

      // ★ 추가: ID 중복 먼저 검사
      const nameTaken = await isCustomerNameTaken(userId);
      if (nameTaken) {
        errorMsg = '이미 존재하는 ID입니다. 다른 값을 사용해 주세요.';
        return;
      }

      // 기존: Password(=customer_id) 중복 검사
      const idTaken = await isCustomerIdTaken(password);
      if (idTaken) {
        errorMsg = '이미 존재하는 Password입니다. 다른 값을 사용해 주세요.';
        return;
      }

      // DB 스키마에 맞춘 페이로드
      const payload = {
        customerId: password,     // PK/유니크
        customerType,             // 'admin' | 'borrower'
        customerName: userId      // 로그인용 ID
      };

      const res = await fetch(`${API_BASE}/api/members`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        const txt = await res.text().catch(() => '');
        if (res.status === 409) {          // 백엔드가 409를 주는 경우 대비
          errorMsg = '이미 존재하는 값이 있습니다. (ID 또는 Password)';
          return;
        }
        throw new Error(txt || `Sign up failed (${res.status})`);
      }

      okMsg = '가입이 완료되었습니다. 로그인 페이지로 이동합니다.';
      setTimeout(() => goto('/login'), 600);
    } catch (err) {
      console.error(err);
      if (!errorMsg) {
        errorMsg = '회원가입에 실패했습니다. 입력값이나 서버 상태를 확인해 주세요.';
      }
    } finally {
      loading = false;
    }
  }
</script>

<svelte:head>
  <title>Activate your account — YELLOW SOCKS</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
</svelte:head>

<main class="wrap">
  <h1 class="title">Activate your account</h1>

  <form class="card" on:submit={onSignUp} autocomplete="off">
    <!-- ✔ customer_name -->
    <div class="field">
      <label for="uid">ID</label>
      <input id="uid" placeholder="Enter your ID" bind:value={userId} required />
    </div>

    <!-- ✔ customer_id -->
    <div class="field">
      <label for="pwd">Password</label>
      <input id="pwd" type="password" placeholder="Enter your Password" bind:value={password} required />
    </div>

    <div class="role">
      <p class="role-title">Select your role</p>
      <div class="role-grid">
        <button type="button" class="role-btn {role==='admin' ? 'active' : ''}" on:click={() => choose('admin')}>Admin</button>
        <button type="button" class="role-btn {role==='user' ? 'active' : ''}" on:click={() => choose('user')}>User</button>
      </div>
      <p class="role-hint"></p>
    </div>

    {#if errorMsg}<p class="msg error">{errorMsg}</p>{/if}
    {#if okMsg}<p class="msg ok">{okMsg}</p>{/if}

    <button class="submit" type="submit" disabled={loading}>
      {#if loading}Signing up…{/if}
      {#if !loading}Sign up{/if}
    </button>
  </form>
</main>

<style>
  :global(html), :global(body){
    background:#fff; margin:0; font-family:system-ui, -apple-system, Segoe UI, Roboto, sans-serif;
  }

  :root{
    --brand:#d1a931;   /* YELLOW SOCKS 골드 */
    --ink:#2b2b2b;
    --muted:#6b7280;
    --border:#e5e7eb;
    --ring:#f3e6a6;
    --card:#ffffff;
  }

  /* 화면 정중앙 정렬 */
  .wrap{
    min-height:100svh;
    display:flex;
    flex-direction:column;
    align-items:center;
    justify-content:center;
    gap:14px;
    padding:24px 16px;
  }

  .title{
    margin:0;
    font-size: clamp(28px, 4vw, 40px);
    color: var(--brand);
    font-weight: 800;
    letter-spacing:.3px;
    text-align:center;
  }

  .card{
    width:min(420px, 92vw);
    background:var(--card);
    border:1px solid var(--border);
    border-radius:14px;
    padding:22px;
    box-shadow:0 14px 40px rgba(16, 30, 55, .08);
  }

  .field{ margin-bottom:16px; }
  label{
    display:block; margin:0 0 6px; font-weight:700; color:#323232; font-size:14px;
  }
  input{
    width:100%; height:44px; border-radius:10px; border:1px solid var(--border);
    padding:0 12px; font-size:14px; outline:none; background:#fafafa; color:#2b2b2b;
    transition:border-color .15s, box-shadow .15s, background .15s;
  }
  input::placeholder{ color:#b7bdc7; }
  input:focus{ background:#fff; border-color:#d9d9d9; box-shadow:0 0 0 3px var(--ring); }

  .role{ margin: 14px 0 8px; }
  .role-title{
    margin:0 0 10px; text-align:center; font-weight:800; color:#363636;
  }
  .role-grid{
    display:grid; grid-template-columns:1fr 1fr; gap:14px;
  }
  .role-btn{
    height:46px; border-radius:12px; border:1px solid #e6e6e6; background:#fff; color:#7a660e;
    font-weight:800; cursor:pointer; transition: border-color .15s, box-shadow .15s, transform .04s;
  }
  .role-btn:hover{ border-color:#eadfb3; }
  .role-btn:active{ transform: translateY(1px); }
  .role-btn.active{
    border-color: var(--brand);
    box-shadow: 0 0 0 3px rgba(209,169,49,.18);
  }
  .role-hint{
    margin:8px 0 0; font-size:12px; color:#777; text-align:center;
  }

  .msg{ margin:8px 0 0; font-size:13px; text-align:center; }
  .msg.error{ color:#dc2626; }
  .msg.ok{ color:#166534; }

  .submit{
    display:block; width:100%; height:46px; margin-top:16px;
    border:1px solid #e7d27b;
    background: var(--brand);
    color:#2d2d2d; font-weight:800; border-radius:12px; cursor:pointer;
    box-shadow:0 8px 22px rgba(209,169,49,.35);
    transition: filter .15s, transform .04s, opacity .15s;
  }
  .submit:hover{ filter:brightness(1.02); }
  .submit:active{ transform: translateY(1px); }
  .submit[disabled]{ opacity:.7; cursor:not-allowed; }
</style>
