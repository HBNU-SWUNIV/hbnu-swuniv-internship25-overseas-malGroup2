<script>
  import { onMount } from 'svelte';

  // 동일 오리진이면 '' 로 변경
  const API_BASE = 'http://localhost:8081';

  let loading = false;
  let loadErr = '';

  // 검색어
  let q = '';

  // 원본 데이터(백엔드)
  let movements = [];
  // 테이블 렌더용
  let rows = [];

  // 선택/편집 상태
  let selected = null;
  let editMode = false;
  let saving = false;

  // 우측 폼 데이터 (Edit 모드에서 사용)
  let form = {
    assetId: '',
    movementType: '',
    dateTakenOut: '',
    expectedReturnDate: '',
    dateReturned: '',
    personTakingAsset: '',
    department: '',
    purpose: '',
    remarks: '',
    conditionAtCheckout: '',
    conditionAtCheckin: ''
  };

  // 날짜 표시/전송 유틸
  const toISO = (v) => {
    if (!v) return '';
    // Date로 파싱 가능하면 ISO로
    const d = new Date(v);
    if (!isNaN(d)) return d.toISOString().slice(0, 10);
    const s = String(v);
    if (/^\d{4}-\d{2}-\d{2}/.test(s)) return s.slice(0, 10);
    if (/^\d{4}\/\d{2}\/\d{2}/.test(s)) return s.replaceAll('/', '-').slice(0, 10);
    return s.slice(0, 10); // 최대한 방어
  };
  const fmtDisplay = (v) => (v ? toISO(v).replaceAll('-', '/') : '—');

  // 통합 검색(간단 포함)
  const match = (r, term) => {
    if (!term) return true;
    const t = term.toLowerCase();
    return [
      r.assetId,
      r.movementId,
      r.type,
      r.dept,
      r.movement,
      r.out,
      r.returned
    ].join(' ').toLowerCase().includes(t);
  };
  $: filtered = rows.filter((r) => match(r, q));

  onMount(load);

  async function load() {
    loading = true;
    loadErr = '';
    try {
      const res = await fetch(`${API_BASE}/api/asset-movements`, {
        headers: { Accept: 'application/json' }
      });
      if (!res.ok) throw new Error(`GET /api/asset-movements -> ${res.status}`);
      const data = await res.json();
      movements = Array.isArray(data) ? data : [];
      rows = movements.map(mapToRow);
      selected = movements[0] ?? null;
      // 선택 변경 시 폼 동기화
      syncFormFromSelected();
    } catch (e) {
      console.error(e);
      loadErr = '이동 내역을 불러오지 못했습니다.';
    } finally {
      loading = false;
    }
  }

  function mapToRow(m) {
    return {
      movementId: m.movementId,
      assetId: m.assetId,
      type: m.movementType ?? '—',          // 화면의 Type = movementType 사용
      dept: m.department ?? '—',
      movement: m.dateReturned ? 'Check-in' : 'Check-out',
      out: fmtDisplay(m.dateTakenOut),
      returned: fmtDisplay(m.dateReturned),
      raw: m
    };
  }

  function openDetail(r) {
    selected = r?.raw ?? null;
    editMode = false;
    syncFormFromSelected();
  }

  function syncFormFromSelected() {
    if (!selected) return;
    form = {
      assetId: selected.assetId ?? '',
      movementType: selected.movementType ?? '',
      dateTakenOut: toISO(selected.dateTakenOut),
      expectedReturnDate: toISO(selected.expectedReturnDate),
      dateReturned: toISO(selected.dateReturned),
      personTakingAsset: selected.personTakingAsset ?? '',
      department: selected.department ?? '',
      purpose: selected.purpose ?? '',
      remarks: selected.remarks ?? '',
      conditionAtCheckout: selected.conditionAtCheckout ?? '',
      conditionAtCheckin: selected.conditionAtCheckin ?? ''
    };
  }

  function startEdit() {
    if (!selected) return;
    editMode = true;
    syncFormFromSelected();
  }

  function cancelEdit() {
    editMode = false;
    syncFormFromSelected();
  }

  async function saveEdit() {
    if (!selected?.movementId) return;
    saving = true;
    try {
      const url = `${API_BASE}/api/asset-movements/${encodeURIComponent(selected.movementId)}`;
      const res = await fetch(url, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });
      if (!res.ok) {
        const msg = await res.text().catch(() => '');
        throw new Error(`PATCH fail ${res.status} ${msg}`);
      }

      // 로컬 상태를 최신화
      const updated = { ...selected, ...form };
      selected = updated;
      // rows/movements 동기화
      movements = movements.map((m) =>
        m.movementId === updated.movementId ? updated : m
      );
      rows = movements.map(mapToRow);

      editMode = false;
      // 알림은 최소화 (원치 않으면 제거)
      // alert('저장되었습니다.');
    } catch (e) {
      console.error(e);
      alert('저장 중 오류가 발생했습니다.');
    } finally {
      saving = false;
    }
  }
</script>

<div class="main">
  <!-- 좌측 사이드바 -->
  <aside class="sidebar">
    <nav class="menu">
      <h3>Asset Menu</h3>
      <a class="menu-item">- Management</a>
      <a class="menu-item active">- Movement</a>

      <h3 class="mt">Notification</h3>
      <ul class="notif">
        <li class="pill"><b>Product Name</b></li>
        <li class="pill"><b>Rental Date</b></li>
        <li class="pill"><b>Return Date</b></li>
        <li class="pill"><b>D-@</b></li>
      </ul>
    </nav>
    <button class="logout-side">logout</button>
  </aside>

  <!-- 중앙 보드 -->
  <section class="board-wrap">
    <h2 class="board-title">Movement</h2>

    <div class="search-wrap">
      <input
        class="search"
        placeholder="Enter the word you want to search for"
        bind:value={q}
      />
      <button class="search-btn" aria-label="search">🔍</button>
    </div>

    <div class="board">
      <table class="tbl">
        <thead>
          <tr>
            <th>Asset-ID</th><th>Type</th><th>Department</th><th>Movement</th>
            <th>Date-Taken-Out</th><th>Date-Returned</th>
          </tr>
        </thead>

        <tbody>
          {#if loading}
            <tr><td colspan="6">로딩 중…</td></tr>
          {:else if loadErr}
            <tr><td colspan="6" style="color:#b91c1c">{loadErr}</td></tr>
          {:else if filtered.length === 0}
            <tr><td colspan="6">데이터가 없습니다.</td></tr>
          {:else}
            {#each filtered as r}
              <tr on:click={() => openDetail(r)} style="cursor:pointer">
                <td>{r.assetId}</td>
                <td>{r.type}</td>
                <td>{r.dept}</td>
                <td>{r.movement}</td>
                <td>{r.out}</td>
                <td>{r.returned}</td>
              </tr>
            {/each}
          {/if}
        </tbody>
      </table>

      <div class="pager">
        <button class="pg small" disabled>◀</button>
        <button class="pg active">1</button>
        <button class="pg" disabled>2</button>
        <button class="pg" disabled>3</button>
        <button class="pg small" disabled>▶</button>
      </div>
    </div>
  </section>

  <!-- 우측 상세/편집 패널 -->
  <aside class="right-panel">
    <div class="panel-head">
      <ul class="dots"><li class="dot"></li><li class="dot"></li><li class="dot"></li></ul>
      <button class="panel-x" aria-label="close" on:click={() => (selected = null, editMode = false)}>✕</button>
    </div>

    {#if !selected}
      <div class="dim">행을 선택하면 상세가 표시됩니다.</div>
    {:else if !editMode}
      <!-- 보기 모드 -->
      <ul class="detail-list">
        <li><b>• Asset ID</b><div class="dim">{selected.assetId ?? '—'}</div></li>
        <li><b>• Movement ID</b><div class="dim">{selected.movementId ?? '—'}</div></li>
        <li><b>• Movement Type</b><div class="dim">{selected.movementType ?? '—'}</div></li>
        <li><b>• Date Taken Out</b><div class="dim">{fmtDisplay(selected.dateTakenOut)}</div></li>
        <li><b>• Person Taking Asset</b><div class="dim">{selected.personTakingAsset ?? '—'}</div></li>
        <li><b>• Faculty / Department</b><div class="dim">{selected.department ?? '—'}</div></li>
        <li><b>• Purpose / Remarks</b><div class="dim">{selected.purpose ?? selected.remarks ?? '—'}</div></li>
        <li><b>• Approval Status</b><div class="dim">—</div></li>
        <li><b>• Condition at Checkout / Check in</b>
          <div class="dim">{fmtDisplay(selected.dateTakenOut)} – {fmtDisplay(selected.expectedReturnDate)}</div>
        </li>
      </ul>
      <button class="edit" on:click={startEdit}>Edit</button>
    {:else}
      <!-- 편집 모드 -->
      <form class="form" on:submit|preventDefault={saveEdit}>
        <div class="field">
          <label>Movement ID</label>
          <input type="text" value={selected.movementId} disabled />
        </div>

        <div class="field">
          <label>Asset ID</label>
          <input type="text" bind:value={form.assetId} />
        </div>

        <div class="field">
          <label>Movement Type</label>
          <select bind:value={form.movementType}>
            <option value="">선택</option>
            <option value="대여">대여</option>
            <option value="반납">반납</option>
            <option value="이동">이동</option>
          </select>
        </div>

        <div class="grid2">
          <div class="field">
            <label>Date Taken Out</label>
            <input type="date" bind:value={form.dateTakenOut} />
          </div>
          <div class="field">
            <label>Expected Return Date</label>
            <input type="date" bind:value={form.expectedReturnDate} />
          </div>
          <div class="field">
            <label>Date Returned</label>
            <input type="date" bind:value={form.dateReturned} />
          </div>
        </div>

        <div class="field">
          <label>Person Taking Asset</label>
          <input type="text" bind:value={form.personTakingAsset} />
        </div>

        <div class="field">
          <label>Department</label>
          <input type="text" bind:value={form.department} />
        </div>

        <div class="field">
          <label>Purpose</label>
          <input type="text" bind:value={form.purpose} />
        </div>

        <div class="field">
          <label>Remarks</label>
          <textarea rows="3" bind:value={form.remarks}></textarea>
        </div>

        <div class="grid2">
          <div class="field">
            <label>Condition at Checkout</label>
            <input type="text" bind:value={form.conditionAtCheckout} />
          </div>
          <div class="field">
            <label>Condition at Checkin</label>
            <input type="text" bind:value={form.conditionAtCheckin} />
          </div>
        </div>

        <div class="btns">
          <button class="edit" type="submit" disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
          <button class="edit secondary" type="button" on:click={cancelEdit} disabled={saving}>Cancel</button>
        </div>
      </form>
    {/if}
  </aside>
</div>

<style>
  .main{
    display:grid;
    grid-template-columns: 260px minmax(0, 1fr) 380px;
    gap:18px; padding:18px; background:#e7d8b6; flex:1;
  }
  /* 사이드바 */
  .sidebar{
    background:#fff; border:1px solid #eadfbe; border-radius:12px;
    padding:18px; display:flex; flex-direction:column; justify-content:space-between;
  }
  .menu h3{ margin:8px 0 10px; color:#6b5218; font-weight:900; font-size:20px; }
  .menu .mt{ margin-top:22px; }
  .menu-item{ display:block; color:#4a3b16; text-decoration:none; margin:6px 0; }
  .menu-item.active{ font-weight:800; }
  .notif{ list-style:none; padding:0; margin:10px 0 0; display:grid; gap:10px; }
  .pill{ background:#f7f1de; border:1px solid #eadfbe; border-radius:12px; padding:10px; color:#6c5a20; }
  .logout-side{ align-self:flex-start; margin-top:20px; background:transparent; border:none; color:#8b7425; cursor:pointer; }

  /* 중앙 보드 */
  .board-wrap{ display:flex; flex-direction:column; gap:12px; }
  .board-title{ margin:0; font-size:34px; font-weight:900; color:#3a2d12; }
  .search-wrap{
    position:relative; width:min(760px, 100%);
    background:#f3ebd4; border:1px solid #e7dbbd; border-radius:30px; padding:12px 48px 12px 18px;
    box-shadow: inset 0 1px 0 rgba(255,255,255,.8);
  }
  .search{ width:100%; height:36px; border:none; outline:none; background:transparent; font-size:14px; color:#6e6e6e; }
  .search-btn{
    position:absolute; right:8px; top:8px; width:40px; height:40px; border:none;
    background:#fff; border-radius:999px; cursor:pointer; box-shadow:0 1px 2px rgba(0,0,0,.08);
  }
  .board{
    background:#e1d2ad8a; border:1px solid #decfa6; border-radius:16px; padding:14px;
    box-shadow: inset 0 1px 0 rgba(255,255,255,.6);
  }
  .tbl{ width:100%; border-collapse:separate; border-spacing:0; background:#fff; border:1px solid #e1d9c6; border-radius:10px; overflow:hidden; }
  .tbl thead th{
    text-align:left; font-weight:800; color:#5b4b2a; font-size:13px; padding:10px 12px; background:#faf7ee; border-bottom:1px solid #eee3c9;
  }
  .tbl tbody td{ font-size:13px; color:#4b463b; padding:10px 12px; border-bottom:1px solid #f2ebd6; }
  .tbl tbody tr:hover{ background:#fffdf6; }

  .pager{ display:flex; gap:8px; justify-content:center; align-items:center; margin-top:10px; }
  .pg{ height:28px; min-width:28px; padding:0 8px; border:1px solid #ded7c7; background:#fff; border-radius:6px; cursor:pointer; }
  .pg.small{ min-width:24px; height:26px; }
  .pg.active{ background:#f3ebd4; border-color:#d6c89f; font-weight:800; }

  /* 우측 패널 */
  .right-panel{
    background:#fff; border:1px solid #eadfbe; border-radius:18px; padding:14px 16px;
    align-self:start; min-height:600px; max-height:66vh; overflow:auto;
  }
  .panel-head{ display:flex; align-items:center; justify-content:space-between; }
  .dots{ display:flex; gap:6px; padding:0; margin:4px 0 10px; list-style:none; }
  .dot{ width:6px; height:6px; border-radius:999px; background:#d1a931; opacity:.75; }
  .panel-x{ border:none; background:#f7f1de; border:1px solid #eadfbe; width:28px; height:28px; border-radius:999px; cursor:pointer; color:#7a6521; }

  .detail-list{ list-style:none; padding:0 0 12px 0; margin:0; display:grid; gap:10px; color:#6b5a20; }
  .detail-list b{ color:#6b5a20; }
  .dim{ color:#3f3f3f; margin-top:4px; word-break:break-word; }

  .edit{
    border:1px solid #e0d6b8; background:#fff7e0; color:#6b5a20;
    border-radius:6px; padding:6px 10px; cursor:pointer; font-size:12px;
  }
  .edit.secondary{
    background:#fff; color:#7b6a3f; border-color:#e0d6b8;
  }

  /* 폼 */
  .form{ display:grid; gap:10px; }
  .grid2{ display:grid; gap:10px; grid-template-columns: 1fr 1fr; }
  .field{ display:grid; gap:6px; }
  .field label{ font-size:12px; color:#6b5a20; font-weight:700; }
  .field input, .field select, .field textarea{
    border:1px solid #e1d9c6; border-radius:8px; padding:8px 10px; font-size:13px;
  }
  .btns{ display:flex; gap:8px; margin-top:6px; }

  @media (max-width: 1180px){
    .main{ grid-template-columns: 220px 1fr; }
    .right-panel{ display:none; }
  }
  @media (max-width: 760px){
    .main{ grid-template-columns: 1fr; }
    .sidebar{ order:-1; }
  }
</style>
